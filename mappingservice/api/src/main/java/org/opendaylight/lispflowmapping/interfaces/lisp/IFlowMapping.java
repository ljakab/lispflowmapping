/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.lispflowmapping.interfaces.lisp;

import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev150820.MapNotify;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev150820.MapRegister;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev150820.MapReply;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev150820.MapRequest;

/**
 * The LISP Mapping Service interface
 */
public interface IFlowMapping {
    /**
     * Handle southbound map-request
     *
     * @param mapRegister
     *            The map-register message
     * @return a map-notify message, if requested in the map-register or null otherwise
     */
    MapNotify handleMapRegister(MapRegister mapRegister);

    /**
     * Handle southbound map-request
     *
     * @param mr
     *            The map-request messages
     * @return a map-reply messages
     */
    MapReply handleMapRequest(MapRequest mr);

    /**
     * Configure LISP mapping service to track and notify of changes mapping requesters
     *
     * @param smr
     *            Configure state of service
     */
     void setShouldUseSmr(boolean smr);
}
