package io.collective;

import java.time.Clock;

// I used https://www.devglan.com/java8/hashmap-custom-implementation-java as reference for
// writing the CustomHashMap

public class SimpleAgedCache {
    CustomHashMap entryMap = new CustomHashMap();;
    Clock clock;

    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
    }
    
    public SimpleAgedCache() {
    }

    class ExpirableEntry {

        Object key;
        Object value;
        int retentionInMillis;
        ExpirableEntry next;
        long createdAt;
    
        public ExpirableEntry(Object key, Object value, int retentionInMillis, ExpirableEntry next){
            this.key = key;
            this.value = value;
            this.retentionInMillis = retentionInMillis;
            this.next = next;
            this.createdAt = System.currentTimeMillis();
        }
    
        public Object getKey() {
            return key;
        }
    
        public void setKey(Object key) {
            this.key = key;
        }
    
        public Object getValue() {
            return value;
        }
    
        public void setValue(Object value) {
            this.value = value;
        }

        public int getRetentionInMillis() {
            return retentionInMillis;
        }
    
        public void setRetentionInMillis(int retentionInMillis) {
            this.retentionInMillis = retentionInMillis;
        }
    
        public ExpirableEntry getNext() {
            return next;
        }
    
        public void setNext(ExpirableEntry next) {
            this.next = next;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }

    class CustomHashMap {
        int capacity = 16; //Initial default capacity
        ExpirableEntry[] table; //Array of Entry object
    
        public CustomHashMap(){
            table = new ExpirableEntry[capacity];
        }
    
        public CustomHashMap(int capacity){
            this.capacity = capacity;
            table = new ExpirableEntry[capacity];
        }
    
        public void put(Object key, Object value, int retentionInMillis){
            int index = index(key);
            ExpirableEntry newExpirableEntry = new ExpirableEntry(key, value, retentionInMillis, null);
            if(table[index] == null){
                table[index] = newExpirableEntry;
            }else {
                ExpirableEntry previousNode = null;
                ExpirableEntry currentNode = table[index];
                while(currentNode != null){
                    if(currentNode.getKey().equals(key)){
                        currentNode.setValue(value);
                        currentNode.setRetentionInMillis(retentionInMillis);
                        break;
                    }
                    previousNode = currentNode;
                    currentNode = currentNode.getNext();
                }
                if(previousNode != null)
                    previousNode.setNext(newExpirableEntry);
                }
        }
    
        public Object get(Object key){
            Object value = null;
            int index = index(key);
            ExpirableEntry entry = table[index];
            while (entry != null){
                if(entry.getKey().equals(key)) {
                    value = entry.getValue();
                    break;
                }
                entry = entry.getNext();
            }
            return value;
        }

        public int size(){
            doCleanup();
            int count = 0;
            for(int i = 0; i < capacity; i++){
                if(table[i] != null){
                    count++;
                }
            }
            return count;
        }
    
        public void remove(Object key){
            int index = index(key);
            ExpirableEntry previous = null;
            ExpirableEntry entry = table[index];
            while (entry != null){
                if(entry.getKey().equals(key)){
                    if(previous == null){
                        entry = entry.getNext();
                        table[index] = entry;
                        return;
                    }else {
                        previous.setNext(entry.getNext());
                        return;
                    }
                }
                previous = entry;
                entry = entry.getNext();
            }
        }

        public void doCleanup(){
            if (clock != null) {
                long now = clock.millis();
                for(int i = 0; i < capacity; i++){
                    if (table[i] != null) {
                        if (now > table[i].getRetentionInMillis() + table[i].getCreatedAt()) {
                            remove(table[i].getKey());
                        }
                    }
                }
            }
        }
    
        private int index(Object key){
            if(key == null){
                return 0;
            }
            return Math.abs(key.hashCode() % capacity);
        }
    }

    public void put(Object key, Object value, int retentionInMillis) {
        entryMap.put(key, value, retentionInMillis);
    }

    public boolean isEmpty() {
        if (entryMap.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public int size() {
        return entryMap.size();
    }

    public Object get(Object key) {
        Object result = entryMap.get(key);
        return result;
    }

}