package com.klaskagan.notify.service;


import com.klaskagan.notify.domain.Message;
import com.klaskagan.notify.domain.MessageStatus;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;
import java.util.Locale;

/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */
public interface MessageService {

    Message find(Long id);

    void delete(Message message);

    Message send(String sender, String senderFullName, String receiver, String title, String content);

    Message send(String sender, String senderFullName, String receiver, Long receiverId, String title, String content);

    List<Message> receiveAll(String receiver);

    List<Message> receiveWithStatus(String receiver, MessageStatus status);

    List<Message> receiveWithStatus(String receiver, MessageStatus status, int limit, String order);

    int countMessages(String receiver);

    int countMessages(String receiver, MessageStatus status);

    void changeMessagesStatus(String receiver, MessageStatus newStatus);

    String localizeMessage(String bundle, String resourceKey);

    String localizeMessage(String bundle, String resourceKey, Locale locale);

    List<Message> search(String receiver, String query);

    /**
     * Send email as system mail.
     * @param to Receiver email.
     * @param subject Subject.
     * @param text Text.
     */
    void sendSystemEmail(List<String> to, String subject, String text);

    /**
     * Send email
     * @param from Email of sender.
     * @param to Email of receiver.
     * @param subject Subject.
     * @param text Text.
     */
    void sendEmail(String from, List<String> to, String subject, String text);

    void sendEmail(SimpleMailMessage messageTemplate);

}
