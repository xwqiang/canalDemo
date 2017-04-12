package com.alibaba.otter.canal.sample;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCanalClientExample {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCanalClientExample.class);

    public static void main(String args[]) {
        // 创建链接
        String ip = AddressUtils.getHostIp();
        ip = "172.21.62.101";
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip,
            11111), "example", "", "");
//        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("172.21.50.65",11111), "example", "canal", "canal");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe("cards\\..*");
//            connector.subscribe(".*\\..*");
            connector.rollback();
            int totalEmtryCount = 12 * 60 * 60;// 12小时没有数据 就停了
            while (emptyCount < totalEmtryCount) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getId(), message.getEntries());
                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

            System.out.println("empty too many times, exit");
        } finally {
            connector.disconnect();
        }
    }

    private static void printEntry(long message_id, List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN
                || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                    e);
            }

            EventType eventType = rowChage.getEventType();
//            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
//                entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
//                entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
//                eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printInfo(message_id, rowData.getBeforeColumnsList(), "DELETE", entry.getHeader().getSchemaName(),
                        entry.getHeader().getTableName());
                } else if (eventType == EventType.INSERT) {
                    printInfo(message_id, rowData.getAfterColumnsList(), "INSERT", entry.getHeader().getSchemaName(),
                        entry.getHeader().getTableName());
                } else {
                    printColumn(rowData.getBeforeColumnsList());
                    printColumn(rowData.getAfterColumnsList());
                    printInfo(message_id, rowData.getBeforeColumnsList(), "UPDATE", entry.getHeader().getSchemaName(),
                        entry.getHeader().getTableName());
                    printInfo(message_id, rowData.getAfterColumnsList(), "UPDATE", entry.getHeader().getSchemaName(),
                        entry.getHeader().getTableName());
                }
            }
        }
    }


    private static void printInfo(long message_id, List<Column> columnList, String eventType, String schemaName,
        String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append(message_id).append(",").append(schemaName).append(",").append(tableName).append(",")
            .append(eventType);
        Iterator<Column> columnIt = columnList.iterator();
        Column column;
        if (columnIt.hasNext()) {
            column = columnIt.next();
            sb.append(",").append(column.getName()).append(":").append(column.getValue());
            while (columnIt.hasNext()) {
                column = columnIt.next();
                sb.append("|").append(column.getName()).append(":") .append(column.getValue());
            }
        }
        System.out.println(sb.toString());
        LOG.info(sb.toString());
    }

    private static void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}