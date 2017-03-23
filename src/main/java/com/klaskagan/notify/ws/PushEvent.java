package com.klaskagan.notify.ws;

/**
 * Push event that
 * @author Viktoras Baracevicius
 * @since 1.0
 */
public interface PushEvent {

    /**
     * <p>Channel name of the connection.</p>
     * <p>For ex: '/message' or '/message/some_specific_username</p>
     * @return channel name
     */
    String getChannel();

    Object getObject();
}
