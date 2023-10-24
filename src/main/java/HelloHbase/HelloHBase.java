package HelloHbase;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;


public class HelloHBase {
    private static final TableName TABLE_NAME = TableName.valueOf("test_table_example");
    private static final byte[] CF_NAME = Bytes.toBytes("test_cf");
    private static final byte[] QUALIFIER = Bytes.toBytes("test_column");
    private static final byte[] ROW_ID = Bytes.toBytes("row01");

    public static void createTable(final Admin admin) throws IOException {
        if(!admin.tableExists(TABLE_NAME)) {
            TableDescriptor desc = TableDescriptorBuilder.newBuilder(TABLE_NAME)
                    .setColumnFamily(ColumnFamilyDescriptorBuilder.of(CF_NAME))
                    .build();
            admin.createTable(desc);
        }
    }

    public static void putRow(final Table table) throws IOException {
        System.out.println("Inserting a row into an HBase Table with the key: " + Bytes.toString(ROW_ID));
        table.put(new Put(ROW_ID).addColumn(CF_NAME, QUALIFIER, Bytes.toBytes("Hello, Hbase!")));
    }

    public static void getRow(final Table table) {
        System.out.println("Retrieving a row from an HBase Table for the key:" + Bytes.toString(ROW_ID));
        Get g = new Get(ROW_ID);
        Result r = null;
        try {
            r = table.get(g);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert r != null;
        byte[] value = r.getValue(CF_NAME, QUALIFIER);
        String output = Bytes.toString(value);
        System.out.println ("The cell value is: " + output);
    }

    public static void main(String[] args) throws IOException {
        Configuration config = HBaseConfiguration.create();

        System.out.println("Creating an HBase Table: " + TABLE_NAME + " with CF: " + Bytes.toString(CF_NAME) + " with Qualifier: " + Bytes.toString(QUALIFIER));
        try (Connection connection = ConnectionFactory.createConnection(config); Admin admin = connection.getAdmin()) {
            createTable(admin);


            try(Table table = connection.getTable(TABLE_NAME)) {
                putRow(table);
                getRow(table);
            }
        }
    }
}
