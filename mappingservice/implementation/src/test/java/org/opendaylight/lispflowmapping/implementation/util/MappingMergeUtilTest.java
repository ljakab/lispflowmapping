/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.lispflowmapping.implementation.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;
import org.opendaylight.lispflowmapping.implementation.config.ConfigIni;
import org.opendaylight.lispflowmapping.lisp.util.LispAddressUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.inet.binary.types.rev160303.IpAddressBinary;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.inet.binary.types.rev160303.Ipv4AddressBinary;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.SiteId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.XtrId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.eid.container.Eid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.locatorrecords.LocatorRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.locatorrecords.LocatorRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.mapping.record.container.MappingRecord;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.mapping.record.container
        .MappingRecordBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.rloc.container.Rloc;

public class MappingMergeUtilTest {

    private static final String IPV4_STRING_1 =         "1.2.3.0";
    private static final String IPV4_STRING_2 =         "1.2.4.0";
    private static final String IPV4_STRING_3 =         "1.2.5.0";
    private static final String IPV4_STRING_4 =         "1.2.6.0";
    private static final String IPV6_STRING =           "1111:2222:3333:4444:5555:6666:7777:8888";
    private static final String IPV4_RLOC_STRING_1 =    "101.101.101.101";
    private static final String IPV4_RLOC_STRING_2 =    "102.102.102.102";
    private static final String IPV4_STRING_PREFIX_24 = "/24";
    private static final String IPV4_STRING_PREFIX_16 = "/16";
    private static final String IPV6_STRING_PREFIX =    "/96";
    private static final Eid IPV4_PREFIX_EID_1 = LispAddressUtil.asIpv4PrefixEid(IPV4_STRING_1 + IPV4_STRING_PREFIX_24);
    private static final Eid IPV4_PREFIX_EID_2 = LispAddressUtil.asIpv4PrefixEid(IPV4_STRING_2 + IPV4_STRING_PREFIX_16);
    private static final Eid IPV6_PREFIX_EID = LispAddressUtil.asIpv6PrefixEid(IPV6_STRING + IPV6_STRING_PREFIX);
    private static final Eid IPV4_EID_1 = LispAddressUtil.asIpv4Eid(IPV4_STRING_1);
    private static final Eid IPV4_EID_2 = LispAddressUtil.asIpv4Eid(IPV4_STRING_2);
    private static final Eid SOURCE_DEST_KEY_EID_1 = LispAddressUtil
            .asSrcDstEid(IPV4_STRING_3, IPV4_STRING_4, 24, 16, 1);
    private static final Rloc IPV4_RLOC_1 = LispAddressUtil.asIpv4Rloc(IPV4_RLOC_STRING_1);
    private static final Rloc IPV4_RLOC_2 = LispAddressUtil.asIpv4Rloc(IPV4_RLOC_STRING_2);
    private static final IpAddressBinary IPV4_SOURCE_RLOC_1 = new IpAddressBinary(
            new Ipv4AddressBinary(new byte[] {1, 1, 1, 1}));

    private static final XtrId XTR_ID_1 = new XtrId(new byte[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

    private static final SiteId SITE_ID_1 = new SiteId(new byte[]{1, 1, 1, 1, 1, 1, 1, 1});

    private static final long REGISTRATION_VALIDITY = ConfigIni.getInstance().getRegistrationValiditySb();

    /**
     * Tests {@link MappingMergeUtil#mappingIsExpired} method.
     */
    @Test
    public void mappingIsExpiredTest() {
        long timestamp = new Date().getTime();
        MappingRecordBuilder mappingRecordBuilder = getDefaultMappingRecordBuilder();
        assertTrue(MappingMergeUtil.mappingIsExpired(mappingRecordBuilder
                .setTimestamp(timestamp - (REGISTRATION_VALIDITY + 1L)).build()));
        assertFalse(MappingMergeUtil.mappingIsExpired(mappingRecordBuilder.setTimestamp(timestamp).build()));
        assertFalse(MappingMergeUtil.mappingIsExpired(mappingRecordBuilder.setTimestamp(null).build()));
    }

    /**
     * Tests {@link MappingMergeUtil#mappingIsExpired} method, throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void mappingIsExpiredTest_throwsNPE() {
        MappingMergeUtil.mappingIsExpired(null);
    }

    /**
     * Tests {@link MappingMergeUtil#timestampIsExpired} method, throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void timestampIsExpiredTest_withDate_throwsNPE() {
        Date date = null;
        MappingMergeUtil.timestampIsExpired(date);
    }

    /**
     * Tests {@link MappingMergeUtil#timestampIsExpired} method, throws NPE.
     */
    @Test
    public void timestampIsExpiredTest_withDate() {
        long timestamp = new Date().getTime();
        assertTrue(MappingMergeUtil.timestampIsExpired(new Date(timestamp - (REGISTRATION_VALIDITY + 1L))));
        assertFalse(MappingMergeUtil.timestampIsExpired(new Date(timestamp)));
    }

    /**
     * Tests {@link MappingMergeUtil#computeNbSbIntersection} method, Nb mask greater than SB mask.
     */
    @Test
    public void computeNbSbIntersectionTest_withMaskableIpv4PrefixEIDs_() {
        MappingRecord NBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV4_PREFIX_EID_1).build();
        MappingRecord SBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV4_PREFIX_EID_2).build();

        // result
        MappingRecord result = (MappingRecord) MappingMergeUtil
                .computeNbSbIntersection(NBmappingRecord, SBmappingRecord);

        assertEquals(IPV4_PREFIX_EID_1, result.getEid());
    }

    /**
     * Tests {@link MappingMergeUtil#computeNbSbIntersection} method, Nb mask less than SB mask.
     */
    @Test
    public void computeNbSbIntersectionTest_withMaskableIpv4PrefixEIDs() {
        MappingRecord NBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV4_PREFIX_EID_2).build();
        MappingRecord SBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV4_PREFIX_EID_1).build();

        // result
        MappingRecord result = (MappingRecord) MappingMergeUtil
                .computeNbSbIntersection(NBmappingRecord, SBmappingRecord);

        assertEquals(IPV4_PREFIX_EID_1, result.getEid());
    }

    /**
     * Tests {@link MappingMergeUtil#computeNbSbIntersection} method with SourceDestKey Eid address type, Ipv4Prefix
     * SB Eid, Nb mask less than SB mask.
     */
    @Test
    public void computeNbSbIntersectionTest_withMaskableSourceDestKeyEIDs_Ipv4SB() {
        MappingRecord NBmappingRecord = getDefaultMappingRecordBuilder().setEid(SOURCE_DEST_KEY_EID_1).build();
        MappingRecord SBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV4_PREFIX_EID_1).build();

        // result
        final MappingRecord result = (MappingRecord) MappingMergeUtil
                .computeNbSbIntersection(NBmappingRecord, SBmappingRecord);
        final Eid eid = LispAddressUtil.asSrcDstEid(IPV4_STRING_3 ,IPV4_STRING_1 ,24 , 24, 1);

        assertEquals(eid, result.getEid());
    }

    /**
     * Tests {@link MappingMergeUtil#computeNbSbIntersection} method with SourceDestKey Eid address type, Ipv6Prefix
     * SB Eid, Nb mask less than SB mask.
     */
    @Test
    public void computeNbSbIntersectionTest_withMaskableSourceDestKeyEIDs_Ipv6SB() {
        MappingRecord NBmappingRecord = getDefaultMappingRecordBuilder().setEid(SOURCE_DEST_KEY_EID_1).build();
        MappingRecord SBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV6_PREFIX_EID).build();

        // result
        final MappingRecord result = (MappingRecord) MappingMergeUtil
                .computeNbSbIntersection(NBmappingRecord, SBmappingRecord);
        final Eid eid = LispAddressUtil.asSrcDstEid(IPV4_STRING_3 ,IPV6_STRING ,24 , 96, 1);

        assertEquals(eid, result.getEid());
    }

    /**
     * Tests {@link MappingMergeUtil#getCommonLocatorRecords} method with empty list of locator records.
     */
    @Test
    public void getCommonLocatorRecords_withEmptyLocatorRecords() {
        MappingRecord NBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV4_EID_2).build();
        MappingRecord SBmappingRecord = getDefaultMappingRecordBuilder().setEid(IPV4_EID_1).build();

        // result
        MappingRecord result = (MappingRecord) MappingMergeUtil
                .computeNbSbIntersection(NBmappingRecord, SBmappingRecord);
        assertEquals(0, result.getLocatorRecord().size());
    }

    /**
     * Tests {@link MappingMergeUtil#getCommonLocatorRecords} method with list of locator records == null.
     */
    @Test
    public void getCommonLocatorRecords_withNullLocatorRecords() {
        MappingRecord NBmappingRecord = getDefaultMappingRecordBuilder().setLocatorRecord(null).build();
        MappingRecord SBmappingRecord = getDefaultMappingRecordBuilder().setLocatorRecord(null).build();

        // result
        MappingRecord result = (MappingRecord) MappingMergeUtil
                .computeNbSbIntersection(NBmappingRecord, SBmappingRecord);
        assertNull(result.getLocatorRecord());
    }

    /**
     * Tests {@link MappingMergeUtil#getCommonLocatorRecords} method, verifies that NB common locator's priority is set
     * to correct value (based on SB locator's priority).
     */
    @Test
    public void getCommonLocatorRecords_priorityCheck() {
        LocatorRecordBuilder NBlocatorRecordBuilder_1 = new LocatorRecordBuilder()
                .setRloc(IPV4_RLOC_1)
                .setPriority((short) 1)
                .setLocatorId("NB-locator-id");
        LocatorRecordBuilder SBlocatorRecordBuilder_1 = new LocatorRecordBuilder()
                .setRloc(IPV4_RLOC_1)
                .setPriority((short) 255)
                .setLocatorId("SB-locator-id");

        LocatorRecordBuilder NBlocatorRecordBuilder_2 = new LocatorRecordBuilder()
                .setRloc(IPV4_RLOC_2)
                .setPriority((short) 1)
                .setLocatorId("NB-locator-id");
        LocatorRecordBuilder SBlocatorRecordBuilder_2 = new LocatorRecordBuilder()
                .setRloc(IPV4_RLOC_2)
                .setPriority((short) 254)
                .setLocatorId("SB-locator-id");

        final MappingRecordBuilder NBmappingRecordBuilder = getDefaultMappingRecordBuilder();
        NBmappingRecordBuilder.getLocatorRecord().add(NBlocatorRecordBuilder_1.build());
        NBmappingRecordBuilder.getLocatorRecord().add(NBlocatorRecordBuilder_2.build());

        final MappingRecordBuilder SBmappingRecordBuilder = getDefaultMappingRecordBuilder();
        SBmappingRecordBuilder.getLocatorRecord().add(SBlocatorRecordBuilder_1.build());
        SBmappingRecordBuilder.getLocatorRecord().add(SBlocatorRecordBuilder_2.build());

        // result
        final MappingRecord result = (MappingRecord) MappingMergeUtil
                .computeNbSbIntersection(NBmappingRecordBuilder.build(), SBmappingRecordBuilder.build());
        final Iterator<LocatorRecord> iterator = result.getLocatorRecord().iterator();
        final LocatorRecord resultLocator_1 = iterator.next();
        final LocatorRecord resultLocator_2 = iterator.next();

        assertEquals(2, result.getLocatorRecord().size());

        assertEquals("NB-locator-id", resultLocator_1.getLocatorId());
        assertEquals(255, (short) resultLocator_1.getPriority()); // priority changed to 255

        assertEquals("NB-locator-id", resultLocator_2.getLocatorId());
        assertEquals(1, (short) resultLocator_2.getPriority());   // priority remains original
    }

    private static MappingRecordBuilder getDefaultMappingRecordBuilder() {
        return new MappingRecordBuilder()
                .setEid(IPV4_PREFIX_EID_1)
                .setLocatorRecord(new ArrayList<>())
                .setRecordTtl(2)
                .setAction(MappingRecord.Action.NativelyForward)
                .setAuthoritative(true)
                .setMapVersion((short) 1)
                .setSiteId(SITE_ID_1)
                .setSourceRloc(IPV4_SOURCE_RLOC_1)
                .setTimestamp(new Date().getTime())
                .setXtrId(XTR_ID_1);
    }
}