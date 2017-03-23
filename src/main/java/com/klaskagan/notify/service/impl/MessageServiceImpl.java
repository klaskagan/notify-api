package com.klaskagan.notify.service.impl;

import com.klaskagan.notify.dao.MessageDao;
import com.klaskagan.notify.domain.Message;
import com.klaskagan.notify.domain.MessageStatus;
import com.klaskagan.notify.service.MessageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */
@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private final Log log = LogFactory.getLog(MessageServiceImpl.class);

    @Autowired
    private MessageDao messageDao;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private SimpleMailMessage systemEmailTemplate;

    @Override
    public Message find(Long id) {
        return messageDao.findById(id);
    }

    @Override
    public Message send(String sender, String senderFullName, final String receiver, String title, String content) {
        boolean valid = validateMessage(sender, senderFullName, receiver, title, content);
        if (valid) {
            Message message = new Message();
            message.setSender(sender);
            message.setSenderFullName(senderFullName);
            message.setReceiver(receiver);
            message.setTitle(title);
            message.setContent(content);
            return messageDao.saveOrUpdate(message);
        }
        return null;
    }

    @Override
    public Message send(String sender, String senderFullName, final String receiver, Long receiverId, String title, String content) {
        boolean valid = validateMessage(sender, senderFullName, receiver, title, content);
        if (valid) {
            Message message = new Message();
            message.setSender(sender);
            message.setSenderFullName(senderFullName);
            message.setReceiver(receiver);
            message.setReceiverId(receiverId);
            message.setTitle(title);
            message.setContent(content);
            return messageDao.saveOrUpdate(message);
        }
        return null;
    }

    private boolean validateMessage(String sender, String senderFullName, String receiver, String title, String content) {
        return !(  sender == null || senderFullName == null || receiver == null
                || sender.isEmpty() || senderFullName.isEmpty() || receiver.isEmpty());
    }

    @Override
    public void delete(Message message) {
        messageDao.delete(message);
    }

    @Override
    public List<Message> receiveAll(final String receiver) {
        return messageDao.findAll(receiver);
    }

    @Override
    public List<Message> receiveWithStatus(final String receiver, MessageStatus status) {
        return messageDao.findByStatus(receiver, status);
    }

    @Override
    public List<Message> receiveWithStatus(final String receiver, MessageStatus status, int limit, String order) {
        return messageDao.findByStatus(receiver, status, limit, order);
    }

    @Override
    public int countMessages(final String receiver) {
        Number number = messageDao.countMessages(receiver);
        return number.intValue();
    }

    @Override
    public int countMessages(final String receiver, MessageStatus status) {
        Number number = messageDao.countMessages(receiver, status);
        return number.intValue();
    }

    @Override
    public void changeMessagesStatus(final String receiver, MessageStatus newStatus) {
        messageDao.changeMessagesStatus(receiver, newStatus);
    }

    @Override
    public String localizeMessage(String bundle, String resourceKey) {
        FacesContext context = FacesContext.getCurrentInstance();
        return ResourceBundle.getBundle(bundle, context.getViewRoot().getLocale()).getString(resourceKey);
    }

    @Override
    public String localizeMessage(String bundle, String resourceKey, Locale locale) {
        return ResourceBundle.getBundle(bundle, locale).getString(resourceKey);
    }

    @Override
    public List<Message> search(final String receiver, String query) {
        return messageDao.search(receiver, query);
    }

    @Override
    public void sendSystemEmail(List<String> to, String subject, String text) {
        String[] emailArray = to.toArray(new String[to.size()]);
        SimpleMailMessage message = new SimpleMailMessage(systemEmailTemplate);
        sendEmail(emailArray, subject, text, message);
    }

    @Override
    public void sendEmail(String from, List<String> to, String subject, String text) {
        String[] emailArray = to.toArray(new String[to.size()]);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        sendEmail(emailArray, subject, text, message);
    }

    @Override
    public void sendEmail(SimpleMailMessage messageTemplate) {
        Assert.notNull(messageTemplate, "message template cannot be null");
        submitEmail(messageTemplate);
    }

    private void sendEmail(String[] to, String subject, String text, SimpleMailMessage message) {
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setSentDate(DateTime.now().toDate());

        submitEmail(message);
    }

    private void submitEmail(SimpleMailMessage messageTemplate) {
        try {
            mailSender.send(messageTemplate);
            log.info("Email sent successfully");
        } catch (MailException e) {
            log.error("There was an error sending and email.", e);
        }
    }

}
