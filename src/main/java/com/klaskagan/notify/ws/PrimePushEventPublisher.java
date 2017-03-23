package com.klaskagan.notify.ws;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @author Viktoras Baracevicius
 * @since 1.0
 */
@Component
@Scope("request")
public class PrimePushEventPublisher implements PushEventPublisher {

    @Override
    public void publish(PushEvent event) {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish(event.getChannel(), event.getObject() != null ? event.getObject() : StringUtils.EMPTY);
    }

}