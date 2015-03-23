package org.opendaylight.lispflowmapping.prefixutil;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.ardverk.collection.ByteArrayKeyAnalyzer;
import org.ardverk.collection.Cursor;
import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;
import org.ardverk.collection.Trie;
import org.ardverk.collection.Tries;

public class PlayGround {

    public static void main(String[] args) {

        Trie<byte[], String> byteArrayTrie = new ByteArrayPatriciaTrie();

        final byte[] key1 = new byte[] { (byte)0x01, (byte)0x01, (byte)0x01 };
        final String value1 = "1.1.1/24";
        byteArrayTrie.put(key1, value1);

        final byte[] key2 = new byte[] { (byte)0x01, (byte)0x02, (byte)0x02 };
        final String value2 = "1.2.2/24";
        byteArrayTrie.put(key2, value2);

        final byte[] key3 = new byte[] { (byte)0x01, (byte)0x01 };
        final String value3 = "1.1/16";
        byteArrayTrie.put(key3, value3);

        final byte[] key4 = new byte[] { (byte)0x01, (byte)0x02, (byte)0x02, (byte)0x02 };
        final String value4 = "1.2.2.2/32";
        byteArrayTrie.put(key4, value4);

        final byte[] key5 = new byte[] { (byte)0x01 };
        final String value5 = "1/8";
        byteArrayTrie.put(key5, value5);

        final byte[] key6 = new byte[] { (byte)0x02 };
        final String value6 = "2/8";
        byteArrayTrie.put(key6, value6);

        final byte[] key7 = new byte[] { (byte)0x01, (byte)0x02 };
        final String value7 = "1.2/16";
        byteArrayTrie.put(key7, value7);

        final byte[] lookupKey = new byte[] { (byte)0x01, (byte)0x02, (byte)0x02, (byte)0x03 };

        //Map.Entry<byte[], String> byteArrayEntry = byteArrayTrie.traverse(null);
        //SortedMap<byte[], String> sm = byteArrayTrie.tailMap(new byte[] { (byte)0x00});
        SortedMap<byte[], String> sm = byteArrayTrie.headMap(lookupKey);
        //System.out.println(getHexValue(byteArrayEntry.getKey()));
        for(Map.Entry<byte[], String> entry1 : sm.entrySet()) {
            InetPrefix prefix = new InetPrefix(entry1.getKey());
            System.out.println(prefix);
        }
        for(Map.Entry<byte[], String> entry1 : sm.entrySet()) {
            InetPrefix prefix = new InetPrefix(entry1.getKey());
            InetPrefix.toStringBinary(prefix);
        }
    }

    public static char[] getHexValue(byte[] array){
        char[] symbols="0123456789ABCDEF".toCharArray();
        char[] hexValue = new char[array.length * 2];

        for(int i=0;i<array.length;i++) {
            int current = array[i] & 0xff;
            hexValue[i*2+1] = symbols[current & 0x0f];
            hexValue[i*2] = symbols[current >> 4];
        }
        return hexValue;
    }
}
