/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.lispflowmapping.southbound.lisp;

import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.data.DataBrokerService;
import org.opendaylight.controller.sal.binding.api.data.DataModificationTransaction;
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

    public void putNode(Node node) {
        logger.info("Adding node " + node.getId().getValue());
        InstanceIdentifier<Nodes> nodesIdentifier = InstanceIdentifier.builder(Nodes.class).toInstance();
        if (dataBroker != null) {
            DataModificationTransaction transaction = dataBroker.beginTransaction();
            transaction.putOperationalData(nodesIdentifier, node);
            transaction.commit();
        } else {
            logger.warn("No data provider configured, could not add node");
        }
    }

    public void publishNodeUpdated(Node node) {
        logger.info("Notifying update of node" + node.getId().getValue());
        InstanceIdentifier<Node> identifier = InstanceIdentifier.builder(Nodes.class).child(Node.class, node.getKey()).toInstance();
        NodeRef nodeRef = new NodeRef(identifier);
        NodeUpdatedBuilder builder = new NodeUpdatedBuilder();
        builder.setId(node.getId());
        builder.setNodeRef(nodeRef);
        notificationProvider.publish(builder.build());
    }

    public static Node getNodeFromLabel(String nodeLabel) {
        NodeId nodeId = new NodeId(LISP_PREFIX + nodeLabel);
        NodeKey nodeKey = new NodeKey(nodeId);
        NodeBuilder builder = new NodeBuilder();
        builder.setId(nodeId);
        builder.setKey(nodeKey);
        return builder.build();
    }

    public void inventoryUpdate(String nodeLabel) {
        logger.info("Updating Inventory with xTR " + nodeLabel);
        Node node = getNodeFromLabel(nodeLabel);
        putNode(node);
        publishNodeUpdated(node);
    }

    public void setNotificationProvider(
            NotificationProviderService notificationProvider) {
        this.notificationProvider = notificationProvider;
    }

    public void setDataBroker(DataBrokerService dataBroker) {
        this.dataBroker = dataBroker;
    }
}