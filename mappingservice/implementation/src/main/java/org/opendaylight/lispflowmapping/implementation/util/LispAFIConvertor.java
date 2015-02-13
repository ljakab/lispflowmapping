/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.lispflowmapping.implementation.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import org.opendaylight.lispflowmapping.type.AddressFamilyNumberEnum;
import org.opendaylight.lispflowmapping.type.LispCanonicalAddressFormatEnum;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.LispAFIAddress;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lcafkeyvalueaddress.KeyBuilder;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lcafkeyvalueaddress.ValueBuilder;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.LispAddressContainer;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.LispAddressContainerBuilder;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.Address;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.DistinguishedName;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.Mac;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv6.Ipv6AddressBuilder;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.lcafkeyvalue.LcafKeyValueAddressAddr;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.lcafkeyvalue.LcafKeyValueAddressAddrBuilder;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.PrimitiveAddress;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.DistinguishedNameBuilder;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.Ipv4Builder;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.MacBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;

public class LispAFIConvertor {

    public static LispAddressContainer toContainer(LispAFIAddress address) {
        if (address instanceof Address) {
            return new LispAddressContainerBuilder().setAddress((Address) address).build();
        } else {
            return null;
        }
    }

    public static LispAddressContainer toContainer(InetAddress address) {
        if (address instanceof Inet4Address) {
            return new LispAddressContainerBuilder().setAddress((Address) asIPAfiAddress(address.getHostAddress())).build();
        }

        if (address instanceof Inet6Address) {
            return new LispAddressContainerBuilder().setAddress((Address) asIPv6AfiAddress(address.getHostAddress())).build();
        }

        return null;
    }

    public static LispAFIAddress toAFI(LispAddressContainer container) {
        return (LispAFIAddress) container.getAddress();
    }

    public static PrimitiveAddress toPrimitive(LispAFIAddress address) {
        if (address instanceof org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv4.Ipv4Address) {
            return new Ipv4Builder().setIpv4Address(((org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv4.Ipv4Address) address).getIpv4Address()).setAfi(address.getAfi()).build();
        }
        if (address instanceof org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv6.Ipv6Address) {
            return new org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.Ipv6Builder()
                    .setIpv6Address(((org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv6.Ipv6Address) address).getIpv6Address()).setAfi(address.getAfi()).build();
        }
        if (address instanceof Mac) {
            return new MacBuilder().setAfi(address.getAfi()).setMacAddress(((org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.mac.MacAddress) address).getMacAddress()).build();
        }
        if (address instanceof DistinguishedName) {
            return new DistinguishedNameBuilder().setAfi(address.getAfi()).setDistinguishedName(((org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.distinguishedname.DistinguishedName) address).getDistinguishedName())
                    .build();
        }
        return null;
    }

    public static LispAddressContainer getIPContainer(String ip) {
        return new LispAddressContainerBuilder().setAddress((Address) asIPAfiAddress(ip)).build();
    }

    public static org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv4.Ipv4Address asIPAfiAddress(String ip) {
        return new org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv4.Ipv4AddressBuilder()
                .setIpv4Address(new Ipv4Address(ip)).setAfi((short) AddressFamilyNumberEnum.IP.getIanaCode()).build();
    }

    public static LcafKeyValueAddressAddr asKeyValue(String key, PrimitiveAddress value) {
        return new LcafKeyValueAddressAddrBuilder()
                .setAfi(AddressFamilyNumberEnum.LCAF.getIanaCode())
                .setLcafType((short) LispCanonicalAddressFormatEnum.KEY_VALUE.getLispCode())
                .setKey(new KeyBuilder().setPrimitiveAddress(
                        new DistinguishedNameBuilder().setAfi(AddressFamilyNumberEnum.DISTINGUISHED_NAME.getIanaCode()).setDistinguishedName(key)
                                .build()).build()).setValue(new ValueBuilder().setPrimitiveAddress(value).build()).build();
    }

    public static org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.Ipv4 asPrimitiveIPAfiAddress(String ip) {
        return new org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.Ipv4Builder()
                .setIpv4Address(new Ipv4Address(ip)).setAfi((short) AddressFamilyNumberEnum.IP.getIanaCode()).build();
    }

    public static org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.mac.MacAddress asMacAfiAddress(String mac) {
        return new org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.mac.MacAddressBuilder()
                .setMacAddress(new MacAddress(mac)).setAfi((short) AddressFamilyNumberEnum.MAC.getIanaCode()).build();
    }

    public static org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.Mac asPrimitiveMacAfiAddress(String mac) {
        return new org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispsimpleaddress.primitiveaddress.MacBuilder()
                .setMacAddress(new MacAddress(mac)).setAfi((short) AddressFamilyNumberEnum.MAC.getIanaCode()).build();
    }

    public static org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.lispaddress.lispaddresscontainer.address.ipv6.Ipv6Address asIPv6AfiAddress(String ip) {
        return new Ipv6AddressBuilder().setIpv6Address(new Ipv6Address(ip)).setAfi((short) AddressFamilyNumberEnum.IP6.getIanaCode()).build();
    }

}
