package com.klaskagan.notify.dao;

import com.klaskagan.notify.domain.Message;
import com.klaskagan.notify.domain.MessageStatus;

import java.util.List;

/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */
public interface MessageDao {

    Message create(Message message);

    Message findById(Long id);

    Message update(Message message);

    Message saveOrUpdate(Message message);

    void delete(Message message);

    List<Message> findAll(final String receiver);

    List<Message> findByStatus(final String receiver, MessageStatus status);

    List<Message> findByStatus(final String receiver, MessageStatus status, int limit, String order);

    Number countMessages(String receiver);

    Number countMessages(String receiver, MessageStatus status);

    void changeMessagesStatus(String receiver, MessageStatus newStatus);

    List<Message> search(final String receiver, String query);
}
