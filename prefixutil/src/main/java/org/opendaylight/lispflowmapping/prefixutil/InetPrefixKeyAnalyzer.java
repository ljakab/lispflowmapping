/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.prefixutil;

import org.ardverk.collection.AbstractKeyAnalyzer;

public class InetPrefixKeyAnalyzer extends AbstractKeyAnalyzer<InetPrefix> {

    /**
     * A singleton instance of {@link ByteKeyAnalyzer}
     */
    public static final InetPrefixKeyAnalyzer INSTANCE = new InetPrefixKeyAnalyzer();

    @Override
    public int bitIndex(InetPrefix arg0, InetPrefix arg1) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isBitSet(InetPrefix arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPrefix(InetPrefix arg0, InetPrefix arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int lengthInBits(InetPrefix arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

}
