package org.opendaylight.lispflowmapping.prefixutil;

import java.util.Map;

import org.ardverk.collection.ByteArrayKeyAnalyzer;
import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;
import org.ardverk.collection.Trie;

public class PlayGround {

    public static void main(String[] args) {

        /*
         *  Play with text based Patricia Trie
         */
        final String person1 = "Anna Kendricks";
        final String person2 = "Alex Whoever";
        final String person3 = "Emma Stone";
        final String person4 = "Patrick Swayze";
        final String person5 = "William Will-i-am";

        // Trie of First Name -> Person
        Trie<String, String> trie = new PatriciaTrie<String, String>(StringKeyAnalyzer.BYTE);
        trie.put("Anna", person1);
        trie.put("Alex", person2);
        trie.put("Emma", person3);
        trie.put("Patrick", person4);
        trie.put("William", person5);

        // Returns Alex
        Map.Entry<String, String> entry = trie.select("Al");
        System.out.println(entry);


        /*
         *  Play with byte array based Patricia Trie
         */
        final byte[] key1 = new byte[] { (byte)0x01, (byte)0x01, (byte)0x01 };
        final String value1 = "1.1.1/24";
        final byte[] key2 = new byte[] { (byte)0x01, (byte)0x02, (byte)0x02 };
        final String value2 = "1.2.2/24";
        final byte[] key3 = new byte[] { (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01 };
        final String value3 = "1.1.1.1/32";
        final byte[] key4 = new byte[] { (byte)0x01, (byte)0x02, (byte)0x02, (byte)0x02 };
        final String value4 = "1.2.2.2/32";
        final byte[] key5 = new byte[] { (byte)0x01 };
        final String value5 = "1/8";

        final byte[] lookupKey = new byte[] { (byte)0x01, (byte)0x02, (byte)0x02, (byte)0x03 };

        // Trie of First Name -> value
        Trie<byte[], String> byteArrayTrie = new PatriciaTrie<byte[], String>(ByteArrayKeyAnalyzer.VARIABLE);
        byteArrayTrie.put(key1, value1);
        byteArrayTrie.put(key2, value2);
        byteArrayTrie.put(key3, value3);
        byteArrayTrie.put(key4, value4);
        byteArrayTrie.put(key5, value5);

        // Returns Alex
        Map.Entry<byte[], String> byteArrayEntry = byteArrayTrie.select(lookupKey);
        System.out.println(byteArrayEntry);
        System.out.println(getHexValue(byteArrayEntry.getKey()));
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
