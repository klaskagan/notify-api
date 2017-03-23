package com.klaskagan.notify.ws;

import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.impl.JSONEncoder;

/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */
public interface NotifyResource {

    @OnMessage(encoders = {JSONEncoder.class})
    String onMessage(String message);

}
