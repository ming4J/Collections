//一个参数的构造函数指定ArrayList初始化的内部数组大小
public ArrayList(int initialCapacity) {
    //判断initialCapacity是否大于零
    if (initialCapacity > 0) { 
        //如果大于零就new 一个Object类型的initialCapacity大小的数组赋值给实际存储数据的数组elementData
        this.elementData = new Object[initialCapacity];
        //如果initialCapacity == 0 就把预先定义好的空数组赋值给elementData
    } else if (initialCapacity == 0) {
        this.elementData = EMPTY_ELEMENTDATA;
    } else {
        //如果initialCapacity的值小于零会抛出不合法的容量值异常
        throw new IllegalArgumentException("Illegal Capacity: "+
                                           initialCapacity);
    }
}

//ArrayList无参的构造函数
public ArrayList() {
    //把预先定义好的空数组赋值给elementData
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}

//ArrayList接收集合类型的构造函数
public ArrayList(Collection<? extends E> c) {
    //调用Collection的toArray方法把数组赋值给elementData
    elementData = c.toArray();
    //更新Size的大小，并且判断size是否为零
    if ((size = elementData.length) != 0) {
        //因为不同的类通过toArray方法返回的不一定是Object类型，这样会导致在调用ArrayList增加Object元素的方法的时候出现错误
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        //所以判断elementData是不是Object数组类型
        if (elementData.getClass() != Object[].class)
        //如果不是的话调用Arrays.copyOf方法来实现把类型变为Object
            elementData = Arrays.copyOf(elementData, size, Object[].class);
    } else {
        // replace with empty array.
        //size是零的话elementData赋值一个默认的空数组
        this.elementData = EMPTY_ELEMENTDATA;
    }
}



public static void Main(){
	List<String> list = Arrays.asList("abc");
	System.out.println(list.getClass());
	Object[] objArray = list.toArray();
	System.out.println(objArray.getClass());
	objArray[0] = new Object();
}


public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;

    //默认的试试大小为啥是10一会儿增加的时候会说到
    private static final int DEFAULT_CAPACITY = 10;

    //用于空实例的共享空数组实例
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * 共享的空数组实例，用于默认大小的空实例。我们将此与EMPTY_ELEMENTDATA区别开来，
     * 以了解添加第一个元素时需要充气多少。
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * 存储ArrayList元素的数组缓冲区。ArrayList的容量是此数组缓冲区的长度。添加第一个元素时，
     * 任何具有elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA的空数组列表将扩展为DEFAULT_CAPACITY。
     */
    transient Object[] elementData; // non-private to simplify nested class access

    //数组有多少个元素
    private int size;
    
    ........
}
    // E e 泛型参数
    public boolean add(E e) {
        //检查内部容量是不是够
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        //各种边界检查，扩容后将e放进数组
        elementData[size++] = e;
        //返回true
        return true;
    }

    //将新元素添加到指定下标
    public void add(int index, E element) {
        //对下标对数组做边界检查
        rangeCheckForAdd(index);
        //传入当前数组Size + 1进行扩容
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        //扩容后将旧数组index 至 数组最后一个元素 拷贝到 新数组index + 1 至 数组最后一个下标下空出index下标 拷贝元素个数就是Size - index
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        //在指定下标插入值
        elementData[index] = element;
        //数组元素个数自增
        size++;
    }

    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void ensureCapacityInternal(int minCapacity) {
        //进一步明确容量
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));//调用calculateCapacity计算容量
    }

    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        //elementData检查是不是一个空数组
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            //是的话比较minCapacity和DEFAULT_CAPACITY那个更大（DEFAULT_CAPACITY默认是10）
            //因此ArrayList不是刚一定义出来Capacity就是10的而是第一次往里丢元素时它扩容为10了
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        //如果不是的空数组时默认就是Size + 1
        return minCapacity;
    }

    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;
        // 如果 minCapacity 大于 elementData 的长度，则进行扩容处理
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            //数组扩容
            grow(minCapacity);
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        //保存数组当前的长度大小
        int oldCapacity = elementData.length;
        //数组的新容量 = 当前数组容量 + （当前数组容量做位运算向右位移一位）
        // 10 = 1010 >> 1 = 0101 = 5
        // 10 + 5 = 15
        // newCapacity = 15
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        //如果新的数组容量newCapacity小于传入的参数要求的最小容量minCapacity，那么新的数组容量以传入的容量参数为准。
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        //如果新的数组容量newCapacity大于数组能容纳的最大元素个数 MAX_ARRAY_SIZE 2^{31}-1-8
        if (newCapacity - MAX_ARRAY_SIZE > 0)
        //判断传入的参数minCapacity是否大于MAX_ARRAY_SIZE
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        // 扩容后进行数组Copy
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        //如果minCapacity < 0证明溢出了Integer的取值范围抛出异常
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        //如果minCapacity大于MAX_ARRAY_SIZE，那么newCapacity等于Integer.MAX_VALUE，否者newCapacity等于MAX_ARRAY_SIZE
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    //添加一个集合
    public boolean addAll(Collection<? extends E> c) {
        //将Collection泛型toArray成Object数组
        Object[] a = c.toArray();
        //获得Object数组长度
        int numNew = a.length;
        //Arraylist内部数组直接暴力扩容到size + Object数组长度的大小
        ensureCapacityInternal(size + numNew);  // Increments modCount
        //把Object数组拷贝到Arraylist内部数组元素
        System.arraycopy(a, 0, elementData, size, numNew);
        //size进行更新为Object数组长度与Size的和
        size += numNew;
        //如果传入的Object数组长度大于零返回真否则为假
        return numNew != 0;
    }

    //指定下标添加集合
    public boolean addAll(int index, Collection<? extends E> c) {
        //检查数组边界
        rangeCheckForAdd(index);
        //Collection转Object数组
        Object[] a = c.toArray();
        //获得数组长度
        int numNew = a.length;
        //依旧暴力扩容
        ensureCapacityInternal(size + numNew);  // Increments modCount
        //需要群移的长度
        int numMoved = size - index;
        //如果长度大于零进行群移
        if (numMoved > 0)
            //旧数组 插入点 新数组 加入点
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);
        //加入
        System.arraycopy(a, 0, elementData, index, numNew);
        //更新size
        size += numNew;
        //如果传入的Object数组长度大于零返回真否则为假
        return numNew != 0;
    }

    //删除指定下标元素
    public E remove(int index) {
        //数组的边界检查
        rangeCheck(index);

        modCount++;
        //将要移除的值拿出来
        E oldValue = elementData(index);
        //计算出要移除元素下标到数组末尾的长度
        int numMoved = size - index - 1;
        //判断长度是否大于零
        if (numMoved > 0)
            //数组拷贝从指定下标的后一位开始到数组末尾，拷贝到指定下标到数组末尾
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        //空出数组最后一个下标赋值null交给GC进行垃圾回收
        elementData[--size] = null; // clear to let GC do its work
        return oldValue;
    }

    //删除指定对象
    public boolean remove(Object o) {
        //形参是否为空
        if (o == null) {
            //for遍历数组
            for (int index = 0; index < size; index++)
                //找到下标值为空的元素
                if (elementData[index] == null) {
                    //删除元素
                    fastRemove(index);
                    return true;
                }
        } else {
            //同上
            for (int index = 0; index < size; index++)
                //equals
                if (o.equals(elementData[index])) {
                    //删除
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    private void fastRemove(int index) {
        modCount++;
        //计算出要移除元素下标到数组末尾的长度
        int numMoved = size - index - 1;
        if (numMoved > 0)
            //数组拷贝从指定下标的后一位开始到数组末尾，拷贝到指定下标到数组末尾
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        //空出数组最后一个下标赋值null交给GC进行垃圾回收
        elementData[--size] = null; // clear to let GC do its work
    }

    private void rangeCheck(int index) {
        //指定下标越界抛出异常
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    //移除集合中包含的元素
    public boolean removeAll(Collection<?> c) {
        //检测对象是否是是个空对象，不是返回对象本身，是空抛出空指针异常
        Objects.requireNonNull(c);
        //调用批量移除
        return batchRemove(c, false);
    }

    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            // elementData = [1,2,3,4,5]
            // c = [3,5]
            // r = 0 w = 0 [1,2,3,4,5]
            // r = 1 w = 1 [1,2,3,4,5]
            // r = 2 w = 2 [1,2,3,4,5]
            // r = 3 w = 2 [1,2,4,4,5]
            // r = 4 w = 3 [1,2,4,5,5]
            for (; r < size; r++)
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            // 我们的情况是 r=size，那什么时候r会不等于size呢，jdk中写了注释，就是在if判断时，调用数组2的contains方法,
            // 可能会抛空指针等异常。这时数组还没有遍历完，那r肯定是小于size的。那没判断的那些数据还要不要处理？
            // 保守起见jdk还是会将他保存在数组中，因为最终w是作为新的size，所以w加上了没处理过的个数size - r。
            if (r != size) {
                System.arraycopy(elementData, r,
                                 elementData, w,
                                 size - r);
                w += size - r;
            }
            if (w != size) {
                // clear to let GC do its work
                // 标记w的位置 循环遍历将下标赋值为null交给GC进行垃圾回收
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                modCount += size - w;
                //更新列表的元素个数
                size = w;
                //是经过修改
                modified = true;
            }
        }
        return modified;
    }

    // 修改指定下标的值
    public E set(int index, E element) {
        // 数组的边界检查
        rangeCheck(index);
        // 将指定下标旧的值拿出来
        E oldValue = elementData(index);
        // 将指定下标赋新值
        elementData[index] = element;
        // 返回旧的值
        return oldValue;
    }

    // 获得指定下标的值
    public E get(int index) {
        // 对数组进行边界的检查
        rangeCheck(index);
        // 调用 elementData 方法
        return elementData(index);
    }

    // 放回数组指定下标的值
    E elementData(int index) {
        return (E) elementData[index];
    }