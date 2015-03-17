/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.prefixutil;

import org.ardverk.collection.PatriciaTrie;

/**
 * Class to represent an IPv4 or IPv6 prefix collection in a Patricia Trie.
 *
 * @author Lorand Jakab
 *
 */
public class InetPrefixTrie extends PatriciaTrie<InetPrefix, Object> {

    private static final long serialVersionUID = 754372246004434915L;

    InetPrefixTrie() {
        super(InetPrefixKeyAnalyzer.INSTANCE);
    }
}
