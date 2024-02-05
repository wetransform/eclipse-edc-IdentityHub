/*
 *  Copyright (c) 2024 Metaform Systems, Inc.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Metaform Systems, Inc. - initial API and implementation
 *
 */

package org.eclipse.edc.identityhub.participantcontext;

import org.eclipse.edc.identithub.did.spi.DidDocumentService;
import org.eclipse.edc.identityhub.spi.ParticipantContextService;
import org.eclipse.edc.identityhub.spi.events.participant.ParticipantContextObservable;
import org.eclipse.edc.identityhub.spi.store.ParticipantContextStore;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.event.EventRouter;
import org.eclipse.edc.spi.security.KeyParserRegistry;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.transaction.spi.TransactionContext;

import java.time.Clock;

import static org.eclipse.edc.identityhub.participantcontext.ParticipantContextExtension.NAME;

@Extension(NAME)
public class ParticipantContextExtension implements ServiceExtension {
    public static final String NAME = "ParticipantContext Extension";

    @Inject
    private ParticipantContextStore participantContextStore;
    @Inject
    private Vault vault;
    @Inject
    private TransactionContext transactionContext;
    @Inject
    private KeyParserRegistry keyParserRegistry;
    @Inject
    private DidDocumentService didDocumentService;
    @Inject
    private Clock clock;
    @Inject
    private EventRouter eventRouter;

    private ParticipantContextObservable participantContextObservable;

    @Override
    public String name() {
        return NAME;
    }

    @Provider
    public ParticipantContextService createParticipantService() {
        return new ParticipantContextServiceImpl(participantContextStore, vault, transactionContext, keyParserRegistry, participantContextObservable());
    }

    @Provider
    public ParticipantContextObservable participantContextObservable() {
        if (participantContextObservable == null) {
            participantContextObservable = new ParticipantContextObservableImpl();
            participantContextObservable.registerListener(new ParticipantContextListenerImpl(clock, eventRouter));
        }
        return participantContextObservable;
    }
}