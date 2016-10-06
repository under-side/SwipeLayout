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
 * �Զ���ViewGroup����ȡ�໬ɾ�����
 * ��Ƹ�����ļ������裺
 * 1��������ViewGroup������item�ľ�̬ͼ
 * 2������VeiwDragHelper��װ�Ĳ����Ծ�̬item�������Ʋ������жϼ��ƶ�����
 * 3����������״̬ȥʵ��item�Ĵ򿪹رղ���
 * 4��д�ӿڣ�ȥʵ����������Ĺ���
 * @author under-side
 * @see FrameLayout
 * @since 6/10
 */
public class SwipeLayout extends FrameLayout {

	// ��ȡ��������
	private View mBackLayout;
	private View mFrontLayout;

	// ʹ��ViewDragHelperȥʵ�ֶ�view�����ƻ�������
	private ViewDragHelper myViewDragHelper;

	//��view�Ŀ��
	private int mScrollRangWidth;
	private int mFrontLayoutWidth;
	private int mFrontLayoutHeight;
	
	String TAG = "SwipeLayout";

	//��ʼ����ǰitem��״̬
	private ItemState mCurrentState=ItemState.ItemClose;
	
	//���ɸ������������ʵ������
	private onItemStateChangedListener mItemStateChangedListener;
	
	//���췽��
	public SwipeLayout(Context context) {
		this(context, null);
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// a.ͨ����̬create������ȡViewDragHelperʵ������
		myViewDragHelper = ViewDragHelper.create(this, 1.0f, myCallback);
		Log.d(TAG, "SwipeLayout called");
	}

	/**
	 * ����ö���࣬�����жϵ�ǰitem��״̬
	 * @author under-side
	 *
	 */
	public enum ItemState
	{
		ItemOpen,ItemClose,ItemDraging;
	}
	
     /**
      * �������û�ȡ�����ӿ�ʵ������
      * @param listener ������ļ������ӿڶ���
      * @author under-side
      */
	public void setItemStateChangedListener(onItemStateChangedListener listener)
	{
		mItemStateChangedListener = listener;
	}
	 /**
     * ����ļ����ӿڣ������ṩ��������е�״̬��Ϣ
     * @author under-side
     */
	public interface onItemStateChangedListener
	{
		/**
		 * ��item���ڿ���״̬ʱ�����ø÷���
		 * @param swipeLayout �����ʵ������
		 * @author under-side
		 */
		public void onItemOpen(SwipeLayout swipeLayout);
		/**
		 * ��item���ڹر�״̬ʱ�����ø÷���
		 * @param swipeLayout �����ʵ������
		 * @author under-side
		 */
		public void onItemClose(SwipeLayout swipeLayout);
		/**
		 * ��item�������ڴ�״̬ʱ�����ø÷���
		 * @param swipeLayout �����ʵ������
		 * @author under-side
		 */
		public void onItemStartOpen(SwipeLayout swipeLayout);
		/**
		 * ��item�������ڹر�״̬ʱ�����ø÷���
		 * @param swipeLayout �����ʵ������
		 * @author under-side
		 */
		public void onItemStartClose(SwipeLayout swipeLayout);
		/**
		 * ��item������ק״̬ʱ�����ø÷���
		 * @param swipeLayout �����ʵ������
		 * @author under-side
		 */
		public void onItemDraging(SwipeLayout swipeLayout);
	}
	/*
	 * ��дonFinishInflate����Ϊ�÷�������XML�в��ֵ���viewȫ������ӵ�layout֮������
	 * �ᱻ���á���ˣ�������������л�ȡ��view�����ò���
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBackLayout = getChildAt(0);
		mFrontLayout = getChildAt(1);
	}

	/*
	 * ��дonlayout���������ڲ��ָò����е���view
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		//Ĭ�ϵ�Ϊ�ر�item״̬
		layoutContent(false);
		
	}
	//���û�ȡ�ľ��ζ���ȥ������View�Ĳ�������
	private void layoutContent(boolean isOpen) {
		
		Rect frontRect =getFrontRect(isOpen);
		mFrontLayout.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
		
		Rect backRect = getBackRect(frontRect);
		mBackLayout.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
		
		//��frontLayout�������ϲ�
		bringChildToFront(mFrontLayout);
	}

	//��ȡmBackLayout�ľ��ζ���
	private Rect getBackRect(Rect frontRect) {
		int left=frontRect.right;
		return new Rect(left, frontRect.top, left+mScrollRangWidth, frontRect.bottom);
	}
	//��ȡmFrontLayout�ľ��ζ���
	private Rect getFrontRect(boolean isOpen) {
		int left=0;
		if(isOpen)
		{
			left=-mScrollRangWidth;
		}
		return new Rect(left, 0, left+mFrontLayoutWidth, mFrontLayoutHeight);
	}

	/*
	 * ��дonSizeChanged��������ȡ��view�Ŀ��
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mScrollRangWidth = mBackLayout.getMeasuredWidth();
		
		mFrontLayoutHeight=mFrontLayout.getMeasuredHeight();
		mFrontLayoutWidth = mFrontLayout.getMeasuredWidth();
		
	}
	
	// b.���ݴ����¼���ViewDragHelper����
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// �������¼�������ViewDragHelper����
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

	// c.ʵ��ViewDragHelper�Ļص�����
	ViewDragHelper.Callback myCallback = new ViewDragHelper.Callback() {

		/*
		 * 1,���ݷ��ؽ��������ǰchild�Ƿ������ק,trueΪ������ק����֮���� child
		 * ��ǰ����ק��View���ɸ��ݸ�view�жϾ����Ƿ������ק pointerId ���ֶ�㴥����id
		 * 
		 * @see
		 * android.support.v4.widget.ViewDragHelper.Callback#tryCaptureView(
		 * android.view.View, int)
		 */
		@Override
		public boolean tryCaptureView(View v, int id) {
			return true;
		}

		// ��capturedChild������ʱ���Ȼ�ȡ����,����.
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}

		/*
		 * ������ק�ķ�Χ, ������ק��������������. ���������˶���ִ���ٶ� getViewVerticalDragRange(View
		 * child)��ʾ���������ק��Χ
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return 0;
		}

		/*
		 * 2. ���ݽ���ֵ ������Ҫ�ƶ�����(����)λ�� (��Ҫ) ��ʱû�з����������ƶ� ����˵����
		 * child: ��ǰ��ק��View 
		 * left:�µ�λ�õĽ���ֵ, dx λ�ñ仯�� 
		 * left = oldLeft + dx;
		 * clampViewPositionVertical(View child, int top, int dy)��ʾ��������ƶ�����ֵ
		 */
		//��view������������
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
		 * 3. ��Viewλ�øı��ʱ��, ����Ҫ�������� (����״̬, ���涯��, �ػ����) ��ʱ,View�Ѿ�������λ�õĸı�
		 * ����˵����changedView �ı�λ�õ�View
		 * left �µ����ֵ ,λ����ϵֵ
		 * dx ˮƽ����仯��
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
			//�����ϰ汾���ֶ�����invalidate���������н�������»���
			invalidate();
		}

		/*
		 * 4. ��View���ͷŵ�ʱ���ͷž�����ָ̧��ʱ, ���������(ִ�ж���) 
		 * ����˵����View releasedChild ���ͷŵ���View
		 *  float xvel ˮƽ������ٶ�, ����Ϊ+
		 *  float yvel ��ֱ������ٶ�, ����Ϊ+
		 */
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
             if(xvel==0&&mFrontLayout.getLeft()<-mScrollRangWidth/2)
             {
            	 //open the item
//            	 Utils.showToast(getContext(), "open");
            	 openItem();
             }
             //�������������ٶȴ���10ʱ���ᴥ������item�Ķ���
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
	 * �÷������ڷַ���item��size�����˸ı�ʱ���жϵ�ǰ״̬���ص��ӿ��еĶ�Ӧ������
	 */
	protected void dispatchStateEvent() {
		//�������������Ϊ�գ����һֱ����onItemDraging����
		if(mItemStateChangedListener!=null)
		{
			mItemStateChangedListener.onItemDraging(this);
		}
		//��¼��һ�ε�״̬
		ItemState preState=mCurrentState;
		mCurrentState=updateState();
		//�����ǰ״̬����һ��״̬��һ�������Ҽ���������Ϊ�գ���ص��ӿڷ���
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
				//�����ǰ״̬Ϊ��ק״̬���������һ�ε�״̬�����ж϶���
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
	 * ����mFrontLayout��������ֵ�������µ�ǰ��item״̬
	 */
	private ItemState updateState()
	{
		//�õ����������븸�������λ�ã�һ�����Ϊxy������
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
	
	//����ViewDragHelper��װ��smoothSlideViewTo����ִ�п�����������,Ĭ��������
	public void openItem()
	{
		openItem(true);
	}
	/**
	 * �Ƿ����ö���Ч����item��Ĭ�ϵ�ʹ�ö���Ч����item
	 * @param isSmooth��true�����ƽ�ȶ��������򣬲�����
	 * @author under-side
	 */
	public void openItem(boolean isSmooth) {
		if(isSmooth)
		{
			int finalLeft = -mScrollRangWidth;
			myViewDragHelper.smoothSlideViewTo(mFrontLayout, finalLeft, 0);
			//ʹ��v4���е�VeiwCompat���postInvalidateOnAnimation��������߼�����
			ViewCompat.postInvalidateOnAnimation(this);
		}else{
			layoutContent(true);
		}
	}
	//����ViewDragHelper��װ��smoothSlideViewTo����ִ�йرն�������
	public void closeItem()
	{
		closeItem(true);
	}
	/**
	 * �Ƿ����ö���Ч���ر�item��Ĭ�ϵ�ʹ�ö���Ч���ر�item
	 * @param isSmooth��true�����ƽ�ȶ��������򣬲�����
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
	
	//���ø÷���ȥ���㶯���Ƿ���������û���������������
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(myViewDragHelper.continueSettling(true))
		{
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
}
