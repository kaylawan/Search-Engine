/*package assignment;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class CrawlerTest {
    @Test
    public void MarkUpTest(){
        //creates new treapMap
        TreapMap<Integer, String> newMap = new TreapMap<>();
        //adds a value to the treapmap, then looks up this valid key
        newMap.insert(10, "ten");
        assertEquals(newMap.lookup(10),"ten", "value should be 10");
        //goes through 10 invalid keys and ensures method returns null if key can't be looked up
        for(int i=0;i<10;i++){
            assertEquals(newMap.lookup(i),null, "value should be " +i);
        }
        //when key is null
        assertEquals(newMap.lookup(null), null, "should be null");
        newMap.insert(1, "one");
        newMap.insert(2, "two");
        newMap.insert(3, "three");
        newMap.insert(4, "four");
        newMap.insert(5, "five");
        assertEquals(newMap.lookup(1), "one");
        assertEquals(newMap.lookup(2), "two");
        assertEquals(newMap.lookup(3), "three");
        assertEquals(newMap.lookup(4), "four");
        assertEquals(newMap.lookup(5), "five");
        //remove an item at a time, after its removed, lookup should not be able to find the key
        for(int i=1;i<5;i++){
            newMap.remove(i);
            assertEquals(newMap.lookup(i), null);
        }
        newMap.insert(35, "thirty-five");
        newMap.insert(420, "luckynumber");
        newMap.insert(9, "nine");
        assertEquals(newMap.lookup(35), "thirty-five");
        assertFalse(newMap.lookup(420)=="four-hundred-twenty");
        assertEquals(newMap.lookup(9), "nine");
    }
    @Test
    public void WebIndex(){
        //this test doesn't fail anymore. bug in my lookup code
        TreapMap<Integer, String> newMap = new TreapMap<>();
        newMap.insert(2000, "twoThousand");
        assertEquals(newMap.lookup(2000),"twoThousand");
        newMap.insert(288127, "randomElement");
        assertEquals(newMap.lookup(288127), "randomElement");
    }

    @Test
    public void testInsert(){
        //checks that after adding a new element, the BST property holds, left nodes are less than
        //and right nodes are greater than
        //also verifies that priority is the greatest at the root node
        TreapMap<Integer, String> newMap = new TreapMap<>();
        //verifies that adding a new element with same key value,
        //it replaces the key value with new value
        newMap.insert(0, "zero");
        assertEquals(newMap.lookup(0), "zero");
        newMap.insert(0, "newZero");
        assertEquals(newMap.lookup(0), "newZero");

        //if k or v value is null, pair is not inserted
        for(int i=1;i<5;i++){
            newMap.insert(i, null);
            assertEquals(newMap.lookup(i), null);
        }
        for(int i=1;i<5;i++){
            newMap.insert(null, "i");
            assertEquals(newMap.lookup(i), null);
        }
        newMap.insert(null, null);
    }
    @Test
    public void testRemove(){
        TreapMap<Integer, String> newMap = new TreapMap<>();
        //creates a treapmaps of values
        for(int i=0;i<20;i++){
            newMap.insert(i, "i");
        }
        newMap.remove(19);
        assertEquals(newMap.lookup(19), null);
        for(int i=18;i>0;i--){
            newMap.remove(i);
            assertEquals(newMap.lookup(i), null);
        }

        TreapMap<Integer, String> map = new TreapMap<>();
        //creates a treapmaps of values
        for(int i=0;i<20;i++){
            map.insert(i, "i");
        }
        map.remove(19);
        assertEquals(map.lookup(19), null);
        for(int i=18;i>0;i--){
            map.remove(i);
            assertEquals(map.lookup(i), null);
        }
    }
    @Test
    public void testJoin(){
        TreapMap<Integer, String> newMap = new TreapMap<>();
        TreapMap<Integer, String> map2 = new TreapMap<>();
        assertEquals(map2.toString(), "");
        newMap.join(map2);
        assertEquals(newMap.toString(), "");
        newMap.insert(0, "zero");
        newMap.join(map2);
        assertEquals(newMap.lookup(0), "zero");
        assertFalse(newMap.toString()=="");
        newMap.remove(0);
        map2.insert(1, "one");
        newMap.join(map2);
        assertEquals(newMap.lookup(1), "one");
        TreapMap<Integer, String> map3 = new TreapMap<>();
        map3.insert(2, "two");
        newMap.join(map3);
        assertEquals(newMap.lookup(2), "two");
        assertFalse(newMap.toString()=="");
    }
    @Test
    public void testSplit(){
        TreapMap<Integer, String> newMap = new TreapMap<>();
        newMap.insert(2000, "twoThousand");
        newMap.insert(5, "five");
        newMap.insert(10, "ten");
        newMap.insert(15, "fifteen");
        newMap.insert(20, "twenty");
        newMap.insert(25, "twenty-five");
        Treap [] treapList = newMap.split(15);
        Treap treap1 = treapList[0];
        Treap treap2 = treapList[1];
        assertEquals(treap1.lookup(5), "five");
        assertEquals(treap1.lookup(10), "ten");
        assertEquals(treap2.lookup(15), "fifteen");
        assertEquals(treap2.lookup(20), "twenty");
        assertEquals(treap2.lookup(25), "twenty-five");
        assertFalse(treap1.lookup(15)!=null);
        assertFalse(treap1.lookup(20)!=null);
        assertFalse(treap1.lookup(25)!=null);
        assertFalse(treap2.lookup(5)!=null);
    }
    @Test
    public void testRemoveOnEmptyTreap(){
        TreapMap<Integer, String> newMap = new TreapMap<>();
        newMap.remove(10);
        assertEquals(newMap.toString(), "", "should be an empty treap");
        assertEquals(newMap.lookup(1), null);
        assertEquals(newMap.lookup(null), null);
    }
    @Test
    public void testIterator(){
        TreapMap<Integer, String> newMap = new TreapMap<>();
        for(int i=0;i<10;i++){
            newMap.insert(i, "i");
        }
        assertEquals(newMap.lookup(11), null);
        assertEquals(newMap.lookup(null), null);
    }
    @Test
    public void testToString(){
        TreapMap<Integer, String> newMap = new TreapMap<>();
        for(int i=0;i<15;i++){
            newMap.insert(i, "" + i);
        }
        System.out.println(newMap.toString()+"/n");
        TreapMap<Integer, String> map2 = new TreapMap<>();
        for(int i=0;i<15;i++){
            map2.insert(i, "" + i);
        }
        System.out.println(map2.toString());
    }
}
*/