public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    //记录LinkedList内部元素个数
    transient int size = 0;

    //Node是LinkedList的内部类表示链表中节点 first实际上是链表的Head头节点
    transient Node<E> first;

    //last实际上是链表的Tail尾节点
    transient Node<E> last;

    
    ..........
}

// 空的构造函数啥也没做
public LinkedList() {

}

// 接收Collection集合类型的构造函数用于将传入Collection类型中的所有元素添加到LinkedList
// 调用空的构造函数创建一个LinkedList对象，调用addAll方法将传入Collection类型中的所有元素添加到LinkedList
public LinkedList(Collection<? extends E> c) {
    this();
    addAll(c);
}

// 添加单个元素到链表中
public boolean add(E e) {
    // 使用尾插法来链接链表
    linkLast(e);
    return true;
}

void linkLast(E e) {
    // 将当前最后一个节点取出
    final Node<E> l = last;
    // new 一个新的节点的,将新节点的前驱指针指向它前面的元素
    final Node<E> newNode = new Node<>(l, e, null);
    // 将last节点赋值为新加入的节点
    last = newNode;
    // 判断last节点是否为空
    if (l == null)
        // 如果为空更新first指针的位置
        first = newNode;
    else
        // 不为空将newNode的前驱节点next指针指向newNode
        l.next = newNode;
    // 元素数量增加
    size++;
    modCount++;
}

private static class Node<E> {
    // 元素的data部分
    E item;
    // 元素的后驱节点
    Node<E> next;
    // 元素的前驱节点
    Node<E> prev;
    // Node的全参构造函数
    Node(Node<E> prev, E element, Node<E> next) {
        // 数据部分赋值
        this.item = element;
        // 后驱
        this.next = next;
        // 前驱
        this.prev = prev;
    }
}

// 在指定的index位置插入元素
public void add(int index, E element) {
    // 检查index边界范围
    checkPositionIndex(index);
    // 如果index等于size那么直接执行尾插入
    if (index == size)
        linkLast(element);
    else
        //通过node方法找到index位置的节点将新元素插入到它前面
        linkBefore(element, node(index));
}

// 传入index
private void checkPositionIndex(int index) {
    if (!isPositionIndex(index))
        // 返回false抛出下标溢出异常
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}

private boolean isPositionIndex(int index) {
    // 当index符合>=0小于等于LinkedList元素个数时返回true
    return index >= 0 && index <= size;
}

Node<E> node(int index) {
    // assert isElementIndex(index);
    // 进行节点查找
    // 如果 index < (size >> 1[就是size的一半])
    // 如果index 比LinkedList元素数的一半小那么从首节点开始查找
    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    } else {
    // 如果index 比LinkedList元素数的一半大就从尾节点开始查找
        Node<E> x = last;
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}

void linkBefore(E e, Node<E> succ) {
    // assert succ != null;
    // 取出前驱指针
    final Node<E> pred = succ.prev;
    // new一个新的节点,将新节点的前驱指针指向,查找到元素的前驱元素,将后驱指针指向查找到的元素
    final Node<E> newNode = new Node<>(pred, e, succ);
    // 将查找到的元素的前驱指针指向新节点
    succ.prev = newNode;
    // 如果之前的前置节点是空，说明succ是原头结点。所以新节点是现在的头结点
    if (pred == null)
        first = newNode;
    else
        // 否则构建前置节点的后置节点为new
        pred.next = newNode;
    // 修改节点数量
    size++;
    modCount++;
}

// 添加Collection集合中的所有元素到LinkedList
public boolean addAll(Collection<? extends E> c) {
    // 将LinKedList的大小和Collection作为参数传递
    return addAll(size, c);
}

public boolean addAll(int index, Collection<? extends E> c) {
    checkPositionIndex(index);//检查越界 [0,size] 闭区间

    Object[] a = c.toArray();//拿到目标集合数组
    int numNew = a.length;//新增元素的数量
    if (numNew == 0)//如果新增元素数量为0，则不增加，并返回false
        return false;

    Node<E> pred, succ;  //index节点的前置节点，后置节点
    if (index == size) { //在链表尾部追加数据
        succ = null;  //size节点（队尾）的后置节点一定是null
        pred = last;//前置节点是队尾
    } else {
        succ = node(index);//取出index节点，作为后置节点
        pred = succ.prev; //前置节点是，index节点的前一个节点
    }
    //链表批量增加，是靠for循环遍历原数组，依次执行插入节点操作。对比ArrayList是通过System.arraycopy完成批量增加的
    for (Object o : a) {//遍历要添加的节点。
        @SuppressWarnings("unchecked") E e = (E) o;
        Node<E> newNode = new Node<>(pred, e, null);//以前置节点 和 元素值e，构建new一个新节点，
        if (pred == null) //如果前置节点是空，说明是头结点
            first = newNode;
        else//否则 前置节点的后置节点设置问新节点
            pred.next = newNode;
        pred = newNode;//步进，当前的节点为前置节点了，为下次添加节点做准备
    }

    if (succ == null) {//循环结束后，判断，如果后置节点是null。 说明此时是在队尾append的。
        last = pred; //则设置尾节点
    } else {
        pred.next = succ; // 否则是在队中插入的节点 ，更新前置节点 后置节点
        succ.prev = pred; //更新后置节点的前置节点
    }

    size += numNew;  // 修改数量size
    modCount++;  //修改modCount
    return true;
}

// 删除指定元素
public boolean remove(Object o) {
    // 判断元素是否为空
    if (o == null) {
        // for遍历List
        for (Node<E> x = first; x != null; x = x.next) {
            // 当元素的数据项为null时删除元素
            if (x.item == null) {
                // 摘链
                unlink(x);
                return true;
            }
        }
    } else {
        // 如果不等于null遍历链表
        for (Node<E> x = first; x != null; x = x.next) {
            // 找点指定元素
            if (o.equals(x.item)) {
                // 进行摘链
                unlink(x);
                return true;
            }
        }
    }
    return false;
}

public E remove(int index) {
    // 边界检查
    checkElementIndex(index);
    // 取出指定下边的节点交给ublink 进行摘链
    return unlink(node(index));
}

E unlink(Node<E> x) {
    // assert x != null;
    // 取出元素值
    final E element = x.item;
    // 取出指定元素的后驱
    final Node<E> next = x.next;
    // 取出指定元素的前驱
    final Node<E> prev = x.prev;

    // 如果前驱为空时证明链表就一个元素，所以元素的后驱肯定为空
    if (prev == null) {
        // 把空值赋值给头节点
        first = next;
    } else {
        // 将指定节点的前驱节点的后驱指向指定节点的后驱节点
        prev.next = next;
        // 指定节点的前驱为空
        x.prev = null;
    }
    // 如果next等于空说明指定节点是最后一个节点
    if (next == null) {
        // 更新尾节点为指定节点的前驱节点
        last = prev;
    } else {
        // 如果next不为空说明移除的不是最后一个元素将 指定节点的后驱节点的前驱指针指向指定节点的前驱指针
        next.prev = prev;
        // 指定节点的后驱为null
        x.next = null;
    }
    // 指定节点的数据项为空
    x.item = null;
    // 元素个数减少
    size--;
    modCount++;
    // 返回删除的节点数据
    return element;
}

// 修改指定下标的元素数据
public E set(int index, E element) {
    // 对index进行边界检查
    checkElementIndex(index);
    // 取出指定下标的node节点
    Node<E> x = node(index);
    // 将old值取出
    E oldVal = x.item;
    // 将新的值赋给元素的数据项
    x.item = element;
    // 返回老的值
    return oldVal;
}

// 取得指定下标的元素
public E get(int index) {
    // 检查指定下标的边界
    checkElementIndex(index);
    // 取出指定下标的节点将节点的数据项返回
    return node(index).item;
}
