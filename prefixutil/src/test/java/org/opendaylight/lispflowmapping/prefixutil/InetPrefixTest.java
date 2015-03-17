/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.prefixutil;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Unit tests for InetPrefix class
 *
 * @author lojakab
 *
 */
public class InetPrefixTest {

    @Test
    public void testSimple() {
        InetPrefix v4prefix = new InetPrefix("192.0.2.0/24");
        TestCase.assertNotNull(v4prefix);
        TestCase.assertEquals("192.0.2.0/24", v4prefix.toString());

        InetPrefix v4address = new InetPrefix("192.0.2.1");
        TestCase.assertNotNull(v4address);
        TestCase.assertEquals("192.0.2.1/32", v4address.toString());

        InetPrefix v6prefix = new InetPrefix("2001:db8::/32");
        TestCase.assertNotNull(v6prefix);
        TestCase.assertEquals("2001:db8::/32", v6prefix.toString());

        InetPrefix v6address = new InetPrefix("2001:db8::1");
        TestCase.assertNotNull(v6address);
        TestCase.assertEquals("2001:db8::1/128", v6address.toString());
    }
}
