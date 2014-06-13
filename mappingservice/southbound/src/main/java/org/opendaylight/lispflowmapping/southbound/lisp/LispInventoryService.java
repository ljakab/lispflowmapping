/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.southbound.lisp;

import java.net.InetAddress;

import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.data.DataBrokerService;
import org.opendaylight.controller.sal.binding.api.data.DataModificationTransaction;
import org.opendaylight.lispflowmapping.implementation.serializer.MapRegisterSerializer;
import org.opendaylight.lispflowmapping.implementation.util.ByteUtil;
import org.opendaylight.yang.gen.v1.lispflowmapping.rev131031.xtrsiteid.XtrSiteId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeUpdatedBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LispInventoryService {
    protected static final Logger logger = LoggerFactory.getLogger(LispInventoryService.class);

    public final static String LISP_PREFIX = "LISP|";
    private NotificationProviderService notificationProvider;
    private DataBrokerService dataBroker;

    private void putNode(Node node) {
        logger.trace("Adding node " + node.getId().getValue());
        InstanceIdentifier<Nodes> nodesIdentifier = InstanceIdentifier.builder(Nodes.class).toInstance();
        if (dataBroker != null) {
            DataModificationTransaction transaction = dataBroker.beginTransaction();
            transaction.putOperationalData(nodesIdentifier, node);
            transaction.commit();
        } else {
            logger.warn("No data provider configured, could not add node");
        }
    }

    private void publishNodeUpdated(Node node) {
        logger.trace("Notifying update of node " + node.getId().getValue());
        InstanceIdentifier<Node> identifier = InstanceIdentifier.builder(Nodes.class).child(Node.class, node.getKey()).toInstance();
        NodeRef nodeRef = new NodeRef(identifier);
        NodeUpdatedBuilder builder = new NodeUpdatedBuilder();
        builder.setId(node.getId());
        builder.setNodeRef(nodeRef);
        notificationProvider.publish(builder.build());
    }

    private static Node getNodeFromLabel(String nodeLabel) {
        NodeId nodeId = new NodeId(nodeLabel);
        NodeKey nodeKey = new NodeKey(nodeId);
        NodeBuilder builder = new NodeBuilder();
        builder.setId(nodeId);
        builder.setKey(nodeKey);
        return builder.build();
    }

    private static String getNodeLabelFromAddress(InetAddress address) {
        return LISP_PREFIX + address.getHostAddress();
    }

    private static String getNodeLabelFromAddressAndXtrId(InetAddress address, XtrSiteId xtrSiteId) {
        String addressLabel = getNodeLabelFromAddress(address);
        if (xtrSiteId == null) {
            return addressLabel;
        }
        return addressLabel + "|" + ByteUtil.bytesToHex(xtrSiteId.getXtrId(), MapRegisterSerializer.Length.XTRID_SIZE);
    }

    public String inventoryUpdate(InetAddress address, XtrSiteId xtrSiteId) {
        String nodeLabel = getNodeLabelFromAddressAndXtrId(address, xtrSiteId);
        logger.debug("Updating Inventory with node " + nodeLabel);
        Node node = getNodeFromLabel(nodeLabel);
        putNode(node);
        publishNodeUpdated(node);
        return nodeLabel;
    }

    public void setNotificationProvider(
            NotificationProviderService notificationProvider) {
        this.notificationProvider = notificationProvider;
    }

    public void setDataBroker(DataBrokerService dataBroker) {
        this.dataBroker = dataBroker;
    }
}