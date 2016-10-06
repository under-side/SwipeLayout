package org.under_side.swipelayout.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 自定义ViewGroup，获取侧滑删除组件
 * 设计该组件的几个步骤：
 * 1，先利用ViewGroup，画出item的静态图
 * 2，利用VeiwDragHelper封装的操作对静态item进行手势操作的判断及移动分析
 * 3，根据三种状态去实现item的打开关闭操作
 * 4，写接口，去实现组件与外界的关联
 * @author under-side
 * @see FrameLayout
 * @since 6/10
 */
public class SwipeLayout extends FrameLayout {

	// 获取布局引用
	private View mBackLayout;
	private View mFrontLayout;

	// 使用ViewDragHelper去实现对view的手势滑动操作
	private ViewDragHelper myViewDragHelper;

	//子view的宽高
	private int mScrollRangWidth;
	private int mFrontLayoutWidth;
	private int mFrontLayoutHeight;
	
	String TAG = "SwipeLayout";

	//初始化当前item的状态
	private ItemState mCurrentState=ItemState.ItemClose;
	
	//生成该组件监听器的实例对象
	private onItemStateChangedListener mItemStateChangedListener;
	
	//构造方法
	public SwipeLayout(Context context) {
		this(context, null);
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// a.通过静态create方法获取ViewDragHelper实例对象
		myViewDragHelper = ViewDragHelper.create(this, 1.0f, myCallback);
		Log.d(TAG, "SwipeLayout called");
	}

	/**
	 * 定义枚举类，用于判断当前item的状态
	 * @author under-side
	 *
	 */
	public enum ItemState
	{
		ItemOpen,ItemClose,ItemDraging;
	}
	
     /**
      * 用于设置获取监听接口实例对象
      * @param listener 该组件的监听器接口对象
      * @author under-side
      */
	public void setItemStateChangedListener(onItemStateChangedListener listener)
	{
		mItemStateChangedListener = listener;
	}
	 /**
     * 定义的监听接口，用于提供外界该组件中的状态信息
     * @author under-side
     */
	public interface onItemStateChangedListener
	{
		/**
		 * 当item处于开启状态时，调用该方法
		 * @param swipeLayout 该组件实例对象
		 * @author under-side
		 */
		public void onItemOpen(SwipeLayout swipeLayout);
		/**
		 * 当item处于关闭状态时，调用该方法
		 * @param swipeLayout 该组件实例对象
		 * @author under-side
		 */
		public void onItemClose(SwipeLayout swipeLayout);
		/**
		 * 当item处于正在打开状态时，调用该方法
		 * @param swipeLayout 该组件实例对象
		 * @author under-side
		 */
		public void onItemStartOpen(SwipeLayout swipeLayout);
		/**
		 * 当item处于正在关闭状态时，调用该方法
		 * @param swipeLayout 该组件实例对象
		 * @author under-side
		 */
		public void onItemStartClose(SwipeLayout swipeLayout);
		/**
		 * 当item处于拖拽状态时，调用该方法
		 * @param swipeLayout 该组件实例对象
		 * @author under-side
		 */
		public void onItemDraging(SwipeLayout swipeLayout);
	}
	/*
	 * 重写onFinishInflate，因为该方法是在XML中布局的子view全部被添加到layout之后立即
	 * 会被调用。因此，可以在这里进行获取子view的引用操作
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBackLayout = getChildAt(0);
		mFrontLayout = getChildAt(1);
	}

	/*
	 * 重写onlayout方法，用于布局该布局中的子view
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		//默认的为关闭item状态
		layoutContent(false);
		
	}
	//利用获取的矩形对象，去设置子View的布局属性
	private void layoutContent(boolean isOpen) {
		
		Rect frontRect =getFrontRect(isOpen);
		mFrontLayout.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
		
		Rect backRect = getBackRect(frontRect);
		mBackLayout.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
		
		//将frontLayout绘制在上层
		bringChildToFront(mFrontLayout);
	}

	//获取mBackLayout的矩形对象
	private Rect getBackRect(Rect frontRect) {
		int left=frontRect.right;
		return new Rect(left, frontRect.top, left+mScrollRangWidth, frontRect.bottom);
	}
	//获取mFrontLayout的矩形对象
	private Rect getFrontRect(boolean isOpen) {
		int left=0;
		if(isOpen)
		{
			left=-mScrollRangWidth;
		}
		return new Rect(left, 0, left+mFrontLayoutWidth, mFrontLayoutHeight);
	}

	/*
	 * 重写onSizeChanged方法，获取子view的宽高
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mScrollRangWidth = mBackLayout.getMeasuredWidth();
		
		mFrontLayoutHeight=mFrontLayout.getMeasuredHeight();
		mFrontLayoutWidth = mFrontLayout.getMeasuredWidth();
		
	}
	
	// b.传递触摸事件给ViewDragHelper处理
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 将动作事件处理交给ViewDragHelper处理
		return myViewDragHelper.shouldInterceptTouchEvent(ev);

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try{
			myViewDragHelper.processTouchEvent(event);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
		
	}

	// c.实现ViewDragHelper的回调方法
	ViewDragHelper.Callback myCallback = new ViewDragHelper.Callback() {

		/*
		 * 1,根据返回结果决定当前child是否可以拖拽,true为可以拖拽，反之不行 child
		 * 当前被拖拽的View，可根据该view判断决定是否可以拖拽 pointerId 区分多点触摸的id
		 * 
		 * @see
		 * android.support.v4.widget.ViewDragHelper.Callback#tryCaptureView(
		 * android.view.View, int)
		 */
		@Override
		public boolean tryCaptureView(View v, int id) {
			return true;
		}

		// 当capturedChild被捕获时，既获取焦点,调用.
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}

		/*
		 * 返回拖拽的范围, 不对拖拽进行真正的限制. 仅仅决定了动画执行速度 getViewVerticalDragRange(View
		 * child)表示纵向方向的拖拽范围
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return 0;
		}

		/*
		 * 2. 根据建议值 修正将要移动到的(横向)位置 (重要) 此时没有发生真正的移动 参数说明：
		 * child: 当前拖拽的View 
		 * left:新的位置的建议值, dx 位置变化量 
		 * left = oldLeft + dx;
		 * clampViewPositionVertical(View child, int top, int dy)表示纵向方向的移动建议值
		 */
		//对view滑动进行限制
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child==mFrontLayout)
			{
				if(left>0)
				{
					return 0;
				}else if(left<-mScrollRangWidth){
					return -mScrollRangWidth;
				}
			}
			else if(child==mBackLayout)
			{
				if(left<mFrontLayoutWidth-mScrollRangWidth)
				{
					return mFrontLayoutWidth-mScrollRangWidth;
				}else if(left>mFrontLayoutWidth){
					return mFrontLayoutWidth;
				}
			}
			return left;
		}

		/*
		 * 3. 当View位置改变的时候, 处理要做的事情 (更新状态, 伴随动画, 重绘界面) 此时,View已经发生了位置的改变
		 * 参数说明：changedView 改变位置的View
		 * left 新的左边值 ,位坐标系值
		 * dx 水平方向变化量
		 */
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {

			if(changedView==mFrontLayout)
			{
				mBackLayout.offsetLeftAndRight(dx);
			}
			else if(changedView==mBackLayout)
			{
				mFrontLayout.offsetLeftAndRight(dx);
			}
			
			dispatchStateEvent();
			//兼容老版本，手动调用invalidate方法，进行界面的重新绘制
			invalidate();
		}

		/*
		 * 4. 当View被释放的时候，释放就是手指抬起时, 处理的事情(执行动画) 
		 * 参数说明：View releasedChild 被释放的子View
		 *  float xvel 水平方向的速度, 向右为+
		 *  float yvel 竖直方向的速度, 向下为+
		 */
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
             if(xvel==0&&mFrontLayout.getLeft()<-mScrollRangWidth/2)
             {
            	 //open the item
//            	 Utils.showToast(getContext(), "open");
            	 openItem();
             }
             //当向左活动的手势速度大于10时将会触发开启item的动作
             else if(xvel<-10)
             {
            	 //open the item
//            	 Utils.showToast(getContext(), "open");
            	 openItem();
             }else{
            	 //close tht item
//            	 Utils.showToast(getContext(), "close");
            	 closeItem();
             }
		}

		public void onViewDragStateChanged(int state) {

		}
	};
	/**
	 * 该方法用于分发当item的size发生了改变时，判断当前状态，回调接口中的对应方法。
	 */
	protected void dispatchStateEvent() {
		//如果监听器对象不为空，则会一直调用onItemDraging方法
		if(mItemStateChangedListener!=null)
		{
			mItemStateChangedListener.onItemDraging(this);
		}
		//记录上一次的状态
		ItemState preState=mCurrentState;
		mCurrentState=updateState();
		//如果当前状态与上一次状态不一样，并且监听器对象不为空，则回调接口方法
		if(mCurrentState!=preState&&mItemStateChangedListener!=null)
		{
			if(mCurrentState==ItemState.ItemOpen)
			{
				mItemStateChangedListener.onItemOpen(this);
			}else if(mCurrentState==ItemState.ItemClose)
			{
				mItemStateChangedListener.onItemClose(this);
			}else if(mCurrentState==ItemState.ItemDraging)
			{
				//如果当前状态为拖拽状态，则根据上一次的状态进行判断动作
				if(preState==ItemState.ItemClose)
				{
					mItemStateChangedListener.onItemStartOpen(this);
				}else if(preState==ItemState.ItemOpen)
				{
					mItemStateChangedListener.onItemClose(this);
				}
			}
		}
	}

	/*
	 * 根据mFrontLayout的左坐标值，来更新当前的item状态
	 */
	private ItemState updateState()
	{
		//得到该组件相对与父组件左侧的位置，一父组件为xy坐标轴
		int left=mFrontLayout.getLeft();
		if(left==0)
		{
			return ItemState.ItemClose;
		}else if(left==-mScrollRangWidth)
		{
			return ItemState.ItemOpen;
		}
		return ItemState.ItemDraging;
	}
	
	//运用ViewDragHelper封装的smoothSlideViewTo方法执行开启动画操作,默认是运用
	public void openItem()
	{
		openItem(true);
	}
	/**
	 * 是否运用动画效果打开item，默认的使用动画效果打开item
	 * @param isSmooth：true则采用平稳动画，否则，不采用
	 * @author under-side
	 */
	public void openItem(boolean isSmooth) {
		if(isSmooth)
		{
			int finalLeft = -mScrollRangWidth;
			myViewDragHelper.smoothSlideViewTo(mFrontLayout, finalLeft, 0);
			//使用v4包中的VeiwCompat类的postInvalidateOnAnimation方法，提高兼容性
			ViewCompat.postInvalidateOnAnimation(this);
		}else{
			layoutContent(true);
		}
	}
	//运用ViewDragHelper封装的smoothSlideViewTo方法执行关闭动画操作
	public void closeItem()
	{
		closeItem(true);
	}
	/**
	 * 是否运用动画效果关闭item，默认的使用动画效果关闭item
	 * @param isSmooth：true则采用平稳动画，否则，不采用
	 * @author over-side
	 */
	public void closeItem(boolean isSmooth) {
		if(isSmooth)
		{
			int finalLeft=0;
			 myViewDragHelper.smoothSlideViewTo(mFrontLayout, finalLeft, 0);
			 ViewCompat.postInvalidateOnAnimation(this);
		}
		else{
			layoutContent(false);
		}
		
	}
	
	//利用该方法去计算动画是否结束，如果没有则继续动画操作
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(myViewDragHelper.continueSettling(true))
		{
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
}
