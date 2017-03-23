package com.klaskagan.notify.dao.impl;

import com.klaskagan.notify.dao.MessageDao;
import com.klaskagan.notify.domain.Message;
import com.klaskagan.notify.domain.MessageStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */
@Repository("messageDao")
public class MessageJpaDaoImpl implements MessageDao {

    private static final Log LOG = LogFactory.getLog(MessageJpaDaoImpl.class);

    private static final String SELECT_MESSAGE_QUERY = "SELECT m FROM Message m WHERE m.receiver = :receiver ";

    private static final String COUNT_TOTAL_MESSAGES_QUERY = "SELECT COUNT(*) FROM Message m WHERE m.receiver = :receiver ";

    private static final String ORDER_BY_SEND_DATE = "order by m.sendDate desc";

    private static final String RECEIVER = "receiver";
    private static final String STATUS = "status";


    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Message create(Message message) {
        em.persist(message);
        return message;
    }

    @Override
    @Transactional(readOnly = true)
    public Message findById(Long id) {
        return em.find(Message.class, id);
    }

    @Override
    @Transactional
    public Message update(Message message) {
        return em.merge(message);
    }

    @Override
    @Transactional
    public Message saveOrUpdate(Message message) {
        Message updatedMessage = message.getId() == null ? create(message) : update(message);
        rebuildIndexes();
        return updatedMessage;
    }

    @Override
    @Transactional
    public void delete(Message message) {
        em.remove(em.contains(message) ? message : em.merge(message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findAll(final String receiver) {
        return em.createQuery(SELECT_MESSAGE_QUERY + ORDER_BY_SEND_DATE, Message.class).setParameter(RECEIVER, receiver)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findByStatus(final String receiver, MessageStatus status) {
        return em.createQuery(SELECT_MESSAGE_QUERY +
                " and m.status = :status " +
                ORDER_BY_SEND_DATE, Message.class)
                .setParameter(RECEIVER, receiver)
                .setParameter(STATUS, status)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findByStatus(final String receiver, MessageStatus status, int limit, String order) {
        return em.createQuery(SELECT_MESSAGE_QUERY + " and m.status = :status order by m.sendDate " + order, Message.class)
                .setParameter(RECEIVER, receiver)
                .setParameter(STATUS, status)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Number countMessages(String receiver, MessageStatus status) {
        String query = COUNT_TOTAL_MESSAGES_QUERY + "and m.status = :status";
        Query q = em.createQuery(query)
                .setParameter(RECEIVER, receiver)
                .setParameter(STATUS, status);
        return (Number) q.getSingleResult();
    }

    @Override
    @Transactional
    public void changeMessagesStatus(String receiver, MessageStatus status) {
        String query = "UPDATE Message m " +
                "SET m.status = :status " +
                "where m.receiver = :receiver " +
                "and m.status != :status ";
        Query q = em.createQuery(query)
                .setParameter(STATUS, status)
                .setParameter(RECEIVER, receiver);
        q.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public Number countMessages(String receiver) {
        Query q = em.createQuery(COUNT_TOTAL_MESSAGES_QUERY).setParameter(RECEIVER, receiver);
        return (Number) q.getSingleResult();
    }

    @Transactional
    public List<Message> search(final String receiver, String query) {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

            QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Message.class).get();
            org.apache.lucene.search.Query luceneQuery = qb
                    .keyword()
                    .onFields("title", "content", "sender", "senderFullName")
                    .ignoreFieldBridge()
                    .matching(query)
                    .createQuery();

            // wrap Lucene query in a javax.persistence.Query
            Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Message.class);

            // execute search
            List<Message> result = jpaQuery.getResultList();

            // TODO temporal approach. Figure out to make lucene query get reversed list from db
            Collections.reverse(result);

            result.removeIf(m -> !m.getReceiver().equals(receiver));
            return result;

        } catch (EmptyQueryException e) {
            LOG.debug("", e);
            return null;
        }
    }

    private void rebuildIndexes() {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            LOG.warn(e + " while rebuilding indexes", e);
        }
    }

}
