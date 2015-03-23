/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.prefixutil;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;

/**
 * Class to represent IPv4 and IPv6 address prefixes, by adding a prefix length
 * to an {@link InetAddress} object.  Prefix specific methods include checking
 * if a prefix contains another one, etc.
 *
 * @author Lorand Jakab
 *
 */
public class InetPrefix {
    private static final short IPV4_MAX_PREFIX_LENGTH = 32;
    private static final short IPV6_MAX_PREFIX_LENGTH = 128;
    private static final String ERR_MSG = "Prefix length %s is not in expected range [0..%s]";
    private static final int PREFIX_DELIMITER = '/';

    protected InetAddress networkAddress;
    protected short prefixLength;

    /**
     * Creates an InetPrefix object from an {@link InetAddress} and a
     * prefix length
     *
     * @param networkAddress
     * @param prefixLength
     */
    public InetPrefix(InetAddress networkAddress, short prefixLength) {
        checkPrefixLengthPreconditions(networkAddress, prefixLength);
        this.networkAddress = networkAddress;
        this.prefixLength = prefixLength;
    }

    /**
     * Creates an InetPrefix object from a {@code String} containing the network
     * address and prefix length parts separated by a forward slash
     *
     * @param prefixString
     */
    public InetPrefix(String prefixString) {
        InetAddress networkAddress = null;
        short prefixLength = -1;

        int pos = prefixString.indexOf(PREFIX_DELIMITER);
        if (pos == -1) {
            // We have no netmask
            networkAddress = InetAddresses.forString(prefixString);
            prefixLength = getMaxPrefixLength(networkAddress);
        } else {
            networkAddress = InetAddresses.forString(prefixString.substring(0, pos));
            prefixLength = Short.parseShort(prefixString.substring(pos + 1));
            checkPrefixLengthPreconditions(networkAddress, prefixLength);
        }

        this.networkAddress = networkAddress;
        this.prefixLength = prefixLength;
    }

    public InetPrefix(byte[] bytes) {
        Preconditions.checkArgument(bytes.length <= 4, "Only IPv4 supported");

        this.prefixLength = (short)(bytes.length * 8);
        byte[] ipv4 = new byte[4];
        for (int i=0; i<bytes.length; i++) {
            ipv4[i] = bytes[i];
        }
        for (int i=bytes.length; i<4; i++) {
            ipv4[i] = 0;
        }
        try {
            this.networkAddress = InetAddress.getByAddress(ipv4);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Checks is the address passed as the argument is matched by the prefix
     * on which the method is called
     *
     * @param addr The prefix to check for inclusion
     * @return {@code true} or {@code false}
     */
    public boolean matchedBy(InetAddress addr) {
        return false;
    }

    public byte[] getByteArray() {
        if ((int)(prefixLength % Byte.SIZE) == 0) {
            int size = (int)(prefixLength / Byte.SIZE);
            return Arrays.copyOfRange(networkAddress.getAddress(), 0, size-1);
        }
        return null;
    }

    /**
     * Getter for networkAddress
     *
     * @return network address as {@link InetAddress}
     */
    public InetAddress getNetworkAddress() {
        return networkAddress;
    }

    /**
     * Setter for networkAddress
     *
     * @param networkAddress
     */
    public void setNetworkAddress(InetAddress networkAddress) {
        this.networkAddress = networkAddress;
    }

    /**
     * Getter for prefixLength
     *
     * @return prefixLength as {@code short}
     */
    public short getPrefixLength() {
        return prefixLength;
    }

    /**
     * Setter for prefixLength
     *
     * @param prefixLength
     */
    public void setPrefixLength(short prefixLength) {
        this.prefixLength = prefixLength;
    }

    private static void checkPrefixLengthPreconditions(InetAddress networkAddress, short prefixLength) {
        if (networkAddress instanceof Inet4Address) {
            Preconditions.checkArgument((prefixLength >= 0)
                    && (prefixLength <= IPV4_MAX_PREFIX_LENGTH),
                    ERR_MSG, prefixLength, IPV4_MAX_PREFIX_LENGTH);
        }

        if (networkAddress instanceof Inet6Address) {
            Preconditions.checkArgument((prefixLength >= 0)
                    && (prefixLength <= IPV6_MAX_PREFIX_LENGTH),
                    ERR_MSG, prefixLength, IPV6_MAX_PREFIX_LENGTH);
        }
    }

    private static short getMaxPrefixLength(InetAddress address) {
        if (address instanceof Inet4Address) {
            return IPV4_MAX_PREFIX_LENGTH;
        } else if (address instanceof Inet6Address) {
            return IPV6_MAX_PREFIX_LENGTH;
        }
        return -1;
    }

    @Override
    public String toString() {
        if (prefixLength == -1) {
            return InetAddresses.toAddrString(networkAddress);
        }
        return InetAddresses.toAddrString(networkAddress) + "/" + prefixLength;
    }

    public static void toStringBinary(InetPrefix prefix) {
        byte [] bytes = prefix.getByteArray();
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0'));
        }
        System.out.println("");
    }
}
