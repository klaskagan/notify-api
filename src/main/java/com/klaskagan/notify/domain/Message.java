package com.klaskagan.notify.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */

@Entity @Indexed
public class Message implements Serializable {

    private static final long serialVersionUID = 6721474295554593230L;

    @Id
    @GeneratedValue
    private Long id;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;

    @Lob
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String content;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String sender;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String senderFullName;

    private String receiver;

    private Long receiverId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    private boolean responded;

    public Message() {
        title          = StringUtils.EMPTY;
        content        = StringUtils.EMPTY;
        sender         = StringUtils.EMPTY;
        receiver       = StringUtils.EMPTY;
        senderFullName = StringUtils.EMPTY;
        status         = MessageStatus.UNREAD;
    }

    public boolean isViewed() {
        return status.equals(MessageStatus.READ);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }

    @PrePersist
    public void prePersist() {
        sendDate = new Date();
    }

    @Override
    public String toString() {
        return title;
    }
}
