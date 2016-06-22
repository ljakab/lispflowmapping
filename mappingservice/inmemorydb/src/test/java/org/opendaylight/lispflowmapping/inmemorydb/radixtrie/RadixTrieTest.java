/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.inmemorydb.radixtrie;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RadixTrieTest {
    private static RadixTrie<Integer> radixTrie4;
    private static RadixTrie<Integer> radixTrie6;

    private static byte[] IP4_DEFAULT;
    private static byte[] IP4_BYTES1;
    private static byte[] IP4_BYTES2;
    private static byte[] IP4_BYTES3;
    private static byte[] IP4_BYTES4;
    private static byte[] IP4_BYTES5;
    private static byte[] IP4_BYTES6;
    private static byte[] IP4_BYTES7;
    private static byte[] IP4_BYTES8;
    private static byte[] IP4_BYTES9;
    private static byte[] IP4_BYTES10;
    private static byte[] IP4_BYTES11;
    private static byte[] IP4_BYTES12;

    ArrayList<byte []> itPrefixList4;
    ArrayList<Integer> itPreflenList4;

    private static byte[] IP6_BYTES1;
    private static byte[] IP6_BYTES2;
    private static byte[] IP6_BYTES3;
    private static byte[] IP6_BYTES4;
    private static byte[] IP6_BYTES5;
    private static byte[] IP6_BYTES6;
    private static byte[] IP6_BYTES7;

    @Before
    public void init() {
        try {
            IP4_DEFAULT = InetAddress.getByName("0.0.0.0").getAddress();
            IP4_BYTES1 = InetAddress.getByName("192.168.0.0").getAddress();
            IP4_BYTES2 = InetAddress.getByName("192.167.0.0").getAddress();
            IP4_BYTES3 = InetAddress.getByName("192.169.0.0").getAddress();
            IP4_BYTES4 = InetAddress.getByName("192.168.1.0").getAddress();
            IP4_BYTES5 = InetAddress.getByName("192.168.2.0").getAddress();
            IP4_BYTES6 = InetAddress.getByName("192.168.1.0").getAddress();
            IP4_BYTES7 = InetAddress.getByName("192.168.1.1").getAddress();
            IP4_BYTES8 = InetAddress.getByName("193.168.1.1").getAddress();
            IP4_BYTES9 = InetAddress.getByName("192.168.3.0").getAddress();
            IP4_BYTES10 = InetAddress.getByName("129.214.0.0").getAddress();
            IP4_BYTES11 = InetAddress.getByName("192.168.2.0").getAddress();
            IP4_BYTES12 = InetAddress.getByName("128.0.0.0").getAddress();

            IP6_BYTES1 = InetAddress.getByName("192:168::0:0").getAddress();
            IP6_BYTES2 = InetAddress.getByName("192:167::0:0").getAddress();
            IP6_BYTES3 = InetAddress.getByName("192:169::0:0").getAddress();
            IP6_BYTES4 = InetAddress.getByName("192:168::1:0").getAddress();
            IP6_BYTES5 = InetAddress.getByName("192:168::2:0").getAddress();
            IP6_BYTES6 = InetAddress.getByName("192:168::1:0").getAddress();
            IP6_BYTES7 = InetAddress.getByName("192:168::1:1").getAddress();

            itPrefixList4 = new ArrayList<byte []>();
            itPrefixList4.add(IP4_BYTES2);
            itPrefixList4.add(IP4_BYTES7);
            itPrefixList4.add(IP4_BYTES4);
            itPrefixList4.add(IP4_BYTES4);
            itPrefixList4.add(IP4_BYTES5);
            itPrefixList4.add(IP4_BYTES1);
            itPrefixList4.add(IP4_BYTES3);

            itPreflenList4 = new ArrayList<Integer>();
            itPreflenList4.add(16);
            itPreflenList4.add(32);
            itPreflenList4.add(25);
            itPreflenList4.add(24);
            itPreflenList4.add(24);
            itPreflenList4.add(16);
            itPreflenList4.add(16);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests v4 CRD operations
     */
    @Test
    public void testIp4() {
        RadixTrie<Integer>.TrieNode res;
        radixTrie4 = new RadixTrie<Integer>(32);
        addIp4Addresses(radixTrie4);

        res = radixTrie4.lookupBest(IP4_BYTES7, 32);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES7));
        assertTrue(res.prefixLength() == 32);
        assertTrue(res.data() == 7);

        res = radixTrie4.lookupBest(IP4_BYTES5, 24);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES5));
        assertTrue(res.prefixLength() == 24);
        assertTrue(res.data() == 5);

        radixTrie4.remove(IP4_BYTES5, 24);
        res = radixTrie4.lookupBest(IP4_BYTES5, 24);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES1));
        assertTrue(res.prefixLength() == 16);
        assertTrue(res.data() == 1);

        radixTrie4.remove(IP4_BYTES4, 24);
        radixTrie4.remove(IP4_BYTES7, 32);
        res = radixTrie4.lookupBest(IP4_BYTES7, 32);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES6));
        assertTrue(res.prefixLength() == 25);
        assertTrue(res.data() == 6);

        radixTrie4.removeAll();
        assertTrue(radixTrie4.getRoot() == null);
    }

    /**
     * Tests iterator
     */
    @Test
    public void testIterator() {
        RadixTrie<Integer>.TrieNode res;
        int i = 0;

        addIp4Addresses(radixTrie4);

        radixTrie4.remove(IP4_BYTES5, 24);
        radixTrie4.remove(IP4_BYTES4, 24);
        radixTrie4.insert(IP4_BYTES5, 24, 1);
        radixTrie4.insert(IP4_BYTES6, 24, 1);

        Iterator<RadixTrie<Integer>.TrieNode> it = radixTrie4.getRoot().iterator();

        while (it.hasNext()) {
            res = it.next();
            assertTrue(Arrays.equals(res.prefix(), itPrefixList4.get(i)));
            assertTrue(res.prefixLength() == itPreflenList4.get(i));
            i++;
        }
    }

    /**
     * Tests v4 CRD operations with 0/0 as root
     */
    @Test
    public void testIp4ZeroRoot() {
        radixTrie4 = new RadixTrie<Integer>(32, true);

        RadixTrie<Integer>.TrieNode res;

        addIp4Addresses(radixTrie4);

        res = radixTrie4.lookupBest(IP4_BYTES7, 32);
        System.out.println(res.asIpPrefix());
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES7));
        assertTrue(res.prefixLength() == 32);

        res = radixTrie4.lookupBest(IP4_BYTES5, 24);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES5));
        assertTrue(res.prefixLength() == 24);

        radixTrie4.remove(IP4_BYTES5, 24);
        res = radixTrie4.lookupBest(IP4_BYTES5, 24);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES1));
        assertTrue(res.prefixLength() == 16);

        radixTrie4.remove(IP4_BYTES4, 24);
        radixTrie4.remove(IP4_BYTES7, 32);
        res = radixTrie4.lookupBest(IP4_BYTES7, 32);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES6));
        assertTrue(res.prefixLength() == 25);

        res = radixTrie4.lookupBest(IP4_BYTES8, 32);
        assertTrue(res == null);

        radixTrie4.insert(IP4_DEFAULT, 0, 0);
        res = radixTrie4.lookupBest(IP4_BYTES8, 32);
        assertTrue(Arrays.equals(res.prefix(), IP4_DEFAULT));

        radixTrie4.removeAll();
        assertTrue(!Arrays.equals(radixTrie4.getRoot().prefix(), IP4_DEFAULT));
        assertTrue(radixTrie4.getRoot().bit == 0);
    }

    /**
     * Tests v4 widest negative prefix
     */
    @Test
    public void testIp4WidestNegativePrefix() {
        radixTrie4 = new RadixTrie<Integer>(32, true);

        RadixTrie<Integer>.TrieNode res;

        addIp4Addresses(radixTrie4);

        radixTrie4.remove(IP4_BYTES5, 24);
        radixTrie4.remove(IP4_BYTES4, 24);

        res = radixTrie4.lookupWidestNegative(IP4_BYTES9, 24);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES11));
        assertTrue(res.prefixLength() == 23);
        res = radixTrie4.lookupWidestNegative(IP4_BYTES10, 16);
        assertTrue(Arrays.equals(res.prefix(), IP4_BYTES12));
        assertTrue(res.prefixLength() == 2);
    }

    /**
     * Tests v6 CRD operations
     * It just makes sure v6 prefix lengths don't generate problems. Therefore it's not as thorough as v4.
     */
    @Test
    public void testIp6() {
        RadixTrie<Integer>.TrieNode res;
        radixTrie6 = new RadixTrie<Integer>(128);

        addIp6Addresses(radixTrie6);

        res = radixTrie6.lookupBest(IP6_BYTES7, 128);
        assertTrue(Arrays.equals(res.prefix(), IP6_BYTES4));
        assertTrue(res.prefixLength() == 113);

        res = radixTrie6.lookupBest(IP6_BYTES5, 112);
        assertTrue(Arrays.equals(res.prefix(), IP6_BYTES5));
        assertTrue(res.prefixLength() == 112);

        radixTrie6.remove(IP6_BYTES5, 112);
        res = radixTrie6.lookupBest(IP6_BYTES5, 112);
        assertTrue(Arrays.equals(res.prefix(), IP6_BYTES1));
        assertTrue(res.prefixLength() == 32);

        radixTrie6.remove(IP6_BYTES4, 112);
        res = radixTrie6.lookupBest(IP6_BYTES7, 128);
        assertTrue(Arrays.equals(res.prefix(), IP6_BYTES6));
        assertTrue(res.prefixLength() == 113);

        radixTrie6.removeAll();
        assertTrue(radixTrie6.getRoot() == null);
    }

    /**
     * Test {@link RadixTrie#removeAll}
     */
    @Test
    public void testRemoveAll() {
        radixTrie4 = new RadixTrie<Integer>(32, true);
        addIp4Addresses(radixTrie4);

        radixTrie4.removeAll();
        Assert.assertEquals(0, radixTrie4.getSize());
    }

    private void addIp4Addresses(RadixTrie<Integer> trie) {
        trie.insert(IP4_BYTES7, 32, 7);
        trie.insert(IP4_BYTES6, 25, 6);
        trie.insert(IP4_BYTES5, 24, 5);
        trie.insert(IP4_BYTES4, 24, 4);
        trie.insert(IP4_BYTES3, 16, 3);
        trie.insert(IP4_BYTES2, 16, 2);
        trie.insert(IP4_BYTES1, 16, 1);
    }

    private void addIp6Addresses(RadixTrie<Integer> trie) {
        trie.insert(IP6_BYTES1, 32, 1);
        trie.insert(IP6_BYTES2, 32, 1);
        trie.insert(IP6_BYTES3, 8, 1);
        trie.insert(IP6_BYTES4, 112, 1);
        trie.insert(IP6_BYTES5, 112, 1);
        trie.insert(IP6_BYTES6, 113, 1);
    }
}