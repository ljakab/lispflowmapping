/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.interfaces.dao;

/**
 * Defines DAO Subkeys
 *
 * @author Florin Coras
 *
 */

public interface SubKeys {
    String AUTH_KEY = "password";
    String RECORD = "address";
    String SUBSCRIBERS = "subscribers";
    String REGDATE = "date";
    String LCAF_SRCDST = "lcaf_srcdst";

    String UNKOWN = "-1";
}
