/*
 * Copyright (c) 2014 Contextream, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.lispflowmapping.lisp.serializer;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.apache.commons.lang3.BooleanUtils;
import org.opendaylight.lispflowmapping.lisp.serializer.address.LispAddressSerializer;
import org.opendaylight.lispflowmapping.lisp.serializer.address.LispAddressSerializerContext;
import org.opendaylight.lispflowmapping.lisp.serializer.exception.LispSerializationException;
import org.opendaylight.lispflowmapping.lisp.util.ByteUtil;
import org.opendaylight.lispflowmapping.lisp.util.LispAddressStringifier;
import org.opendaylight.lispflowmapping.lisp.util.LispAddressUtil;
import org.opendaylight.lispflowmapping.lisp.util.MaskUtil;
import org.opendaylight.lispflowmapping.lisp.util.NumberUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.MapRequest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.MessageType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.eid.container.Eid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.eid.list.EidItem;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.eid.list.EidItemBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.eid.list.EidItemKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.maprequest.ItrRloc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.maprequest.ItrRlocBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.maprequest.ItrRlocKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.maprequest.SourceEidBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.maprequestnotification.MapRequestBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105.rloc.container.Rloc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class deals with deserializing map request from udp to the java object.
 */
public final class MapRequestSerializer {

    private static final MapRequestSerializer INSTANCE = new MapRequestSerializer();
    protected static final Logger LOG = LoggerFactory.getLogger(MapRequestSerializer.class);

    // Private constructor prevents instantiation from other classes
    private MapRequestSerializer() {
    }

    public static MapRequestSerializer getInstance() {
        return INSTANCE;
    }

    public ByteBuffer serialize(MapRequest mapRequest) {
        int size = Length.HEADER_SIZE;
        if (mapRequest.getSourceEid() != null && mapRequest.getSourceEid().getEid() != null) {
            size += LispAddressSerializer.getInstance().getAddressSize(mapRequest.getSourceEid().getEid());
        } else {
            size += 2;
        }
        if (mapRequest.getItrRloc() != null) {
            for (ItrRloc address : mapRequest.getItrRloc()) {
                size += LispAddressSerializer.getInstance().getAddressSize(address.getRloc());
            }
        }
        if (mapRequest.getEidItem() != null) {
            for (EidItem record : mapRequest.getEidItem()) {
                size += 2 + LispAddressSerializer.getInstance().getAddressSize(record.getEid());
            }
        }
        ByteBuffer requestBuffer = ByteBuffer.allocate(size);
        requestBuffer.put((byte) ((byte) (MessageType.MapRequest.getIntValue() << 4)
                | ByteUtil.boolToBit(BooleanUtils.isTrue(mapRequest.isAuthoritative()), Flags.AUTHORITATIVE)
                | ByteUtil.boolToBit(BooleanUtils.isTrue(mapRequest.isMapDataPresent()), Flags.MAP_DATA_PRESENT)
                | ByteUtil.boolToBit(BooleanUtils.isTrue(mapRequest.isProbe()), Flags.PROBE) | ByteUtil.boolToBit(
                BooleanUtils.isTrue(mapRequest.isSmr()), Flags.SMR)));
        requestBuffer.put((byte) (ByteUtil.boolToBit(BooleanUtils.isTrue(mapRequest.isPitr()), Flags.PITR)
                | ByteUtil.boolToBit(BooleanUtils.isTrue(mapRequest.isSmrInvoked()), Flags.SMR_INVOKED)));
        if (mapRequest.getItrRloc() != null) {
            int irc = mapRequest.getItrRloc().size();
            if (irc > 0) {
                irc--;
            }
            requestBuffer.put((byte) (irc));
        } else {
            requestBuffer.put((byte) 0);

        }
        if (mapRequest.getEidItem() != null) {
            requestBuffer.put((byte) mapRequest.getEidItem().size());
        } else {
            requestBuffer.put((byte) 0);

        }
        requestBuffer.putLong(NumberUtil.asLong(mapRequest.getNonce()));
        if (mapRequest.getSourceEid() != null && mapRequest.getSourceEid().getEid() != null) {
            LispAddressSerializer.getInstance().serialize(requestBuffer, mapRequest.getSourceEid().getEid());
        } else {
            requestBuffer.putShort((short) 0);
        }
        if (mapRequest.getItrRloc() != null) {
            for (ItrRloc address : mapRequest.getItrRloc()) {
                LispAddressSerializer.getInstance().serialize(requestBuffer, address.getRloc());
            }
        }
        if (mapRequest.getEidItem() != null) {
            for (EidItem record : mapRequest.getEidItem()) {
                requestBuffer.put((byte) 0);
                requestBuffer.put((byte) MaskUtil.getMaskForAddress(record.getEid().getAddress()));
                LispAddressSerializer.getInstance().serialize(requestBuffer, record.getEid());
            }
        }
        if (mapRequest.getMapReply() != null) {
            ByteBuffer replyBuffer = ByteBuffer.allocate(MappingRecordSerializer.getInstance()
                    .getSerializationSize(mapRequest.getMapReply().getMappingRecord()));
            MappingRecordSerializer.getInstance().serialize(replyBuffer, mapRequest.getMapReply().getMappingRecord());
            ByteBuffer combinedBuffer = ByteBuffer.allocate(requestBuffer.capacity() + replyBuffer.capacity());
            combinedBuffer.put(requestBuffer.array());
            combinedBuffer.put(replyBuffer.array());
            return combinedBuffer;
        }
        return requestBuffer;
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    public MapRequest deserialize(ByteBuffer requestBuffer, InetAddress sourceRloc) {
        try {
            final byte typeAndFlags = requestBuffer.get();
            final int type = typeAndFlags >> 4;
            if (MessageType.forValue(type) != MessageType.MapRequest) {
                throw new LispSerializationException("Expected Map-Request packet (type 1), but was type " + type);
            }

            MapRequestBuilder builder = new MapRequestBuilder();
            builder.setAuthoritative(ByteUtil.extractBit(typeAndFlags, Flags.AUTHORITATIVE));
            builder.setMapDataPresent(ByteUtil.extractBit(typeAndFlags, Flags.MAP_DATA_PRESENT));
            builder.setProbe(ByteUtil.extractBit(typeAndFlags, Flags.PROBE));
            builder.setSmr(ByteUtil.extractBit(typeAndFlags, Flags.SMR));

            final byte moreFlags = requestBuffer.get();
            builder.setPitr(ByteUtil.extractBit(moreFlags, Flags.PITR));
            builder.setSmrInvoked(ByteUtil.extractBit(moreFlags, Flags.SMR_INVOKED));

            final int itrCount = ByteUtil.getUnsignedByte(requestBuffer) + 1;
            final int recordCount = ByteUtil.getUnsignedByte(requestBuffer);
            builder.setNonce(requestBuffer.getLong());
            LispAddressSerializerContext ctx = new LispAddressSerializerContext(
                    LispAddressSerializerContext.MASK_LEN_MISSING);
            builder.setSourceEid(new SourceEidBuilder().setEid(
                    LispAddressSerializer.getInstance().deserializeEid(requestBuffer, ctx)).build());

            if (builder.getItrRloc() == null) {
                builder.setItrRloc(new ArrayList<ItrRloc>());
            }
            for (int i = 0; i < itrCount; i++) {
                Rloc rloc = LispAddressSerializer.getInstance().deserializeRloc(requestBuffer);
                builder.getItrRloc().add(new ItrRlocBuilder()
                        .withKey(new ItrRlocKey(LispAddressStringifier.getString(rloc)))
                        .setRloc(rloc).build());
            }

            if (builder.getEidItem() == null) {
                builder.setEidItem(new ArrayList<EidItem>());
            }
            for (int i = 0; i < recordCount; i++) {
                Eid eid = EidRecordSerializer.getInstance().deserialize(requestBuffer);
                builder.getEidItem().add(new EidItemBuilder()
                        .withKey(new EidItemKey(LispAddressStringifier.getString(eid)))
                        .setEid(eid).build());
            }
            if (builder.isMapDataPresent() && requestBuffer.hasRemaining()) {
                try {
                    builder.setMapReply(new org.opendaylight.yang.gen.v1.urn.opendaylight.lfm.lisp.proto.rev151105
                            .maprequest.MapReplyBuilder().setMappingRecord(
                            MappingRecordSerializer.getInstance().deserialize(requestBuffer)).build()).build();
                } catch (RuntimeException re) {
                    LOG.warn("Couldn't deserialize Map-Reply encapsulated in Map-Request", re);
                }
            }
            builder.setSourceRloc(LispAddressUtil.addressBinaryFromInet(sourceRloc));
            return builder.build();
        } catch (RuntimeException re) {
            throw new LispSerializationException("Couldn't deserialize Map-Request (len="
                    + requestBuffer.capacity() + ")", re);
        }
    }

    public interface Flags {
        byte AUTHORITATIVE = 0x08;
        byte MAP_DATA_PRESENT = 0x04;
        byte PROBE = 0x02;
        byte SMR = 0x01;

        byte PITR = (byte) 0x80;
        byte SMR_INVOKED = 0x40;
    }

    private interface Length {
        int HEADER_SIZE = 12;
    }

}
