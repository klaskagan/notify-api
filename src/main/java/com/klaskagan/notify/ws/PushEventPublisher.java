package com.klaskagan.notify.ws;

/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */
public interface PushEventPublisher {

    void publish(PushEvent event);

}
