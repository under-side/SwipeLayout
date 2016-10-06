package org.under_side.swipelayout.adapter;

import java.util.ArrayList;

import org.under_side.swipelayout.ui.SwipeLayout;
import org.under_side.swipelayout.ui.SwipeLayout.onItemStateChangedListener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.swipelayout.R;

public class ListViewAdapter extends BaseAdapter {

	private ArrayList<String> nameList;
	private Context context;

	public ListViewAdapter(Context context, ArrayList<String> nameList) {
		this.nameList = nameList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return nameList.size();
	}

	@Override
	public Object getItem(int position) {
		return nameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SwipeLayout view = (SwipeLayout) convertView;
		if (view == null) {
			view = (SwipeLayout) View
					.inflate(context, R.layout.item_list, null);

			implementsItemStateChangedListener(view);
		}
		ViewHolder holder = ViewHolder.getViewHolder(view);

		// 填充view
		addDataToView(holder, position);

		return view;
	}

	ArrayList<SwipeLayout> itemCount = new ArrayList<SwipeLayout>();

	// 实现该组件中的监听器方法，实现回调
	private void implementsItemStateChangedListener(SwipeLayout view) {
		view.setItemStateChangedListener(new onItemStateChangedListener() {
			@Override
			public void onItemStartOpen(SwipeLayout swipeLayout) {
				//当将要开启一个item时，去遍历已经打开的layout，去执行关闭操作
				for (SwipeLayout layout : itemCount) {
                     layout.closeItem();
				}
				itemCount.clear();
			}

			@Override
			public void onItemStartClose(SwipeLayout swipeLayout) {
			}

			@Override
			public void onItemOpen(SwipeLayout swipeLayout) {
				itemCount.add(swipeLayout);
			}

			@Override
			public void onItemDraging(SwipeLayout swipeLayout) {

			}

			@Override
			public void onItemClose(SwipeLayout swipeLayout) {
				itemCount.remove(swipeLayout);
			}
		});
	}

	private void addDataToView(ViewHolder holder, int position) {
		holder.nameText.setText(nameList.get(position));
	}

	static class ViewHolder {

		TextView delText, callText;
		TextView nameText;

		// 根据view获取VeiwHolder
		public static ViewHolder getViewHolder(View view) {
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				initViewHolder(view, holder);
				return (ViewHolder) view.getTag();
			}
			return holder;
		}

		// 初始化view
		private static void initViewHolder(View view, ViewHolder holder) {
			holder = new ViewHolder();
			holder.callText = (TextView) view.findViewById(R.id.tv_call);
			holder.delText = (TextView) view.findViewById(R.id.tv_del);
			holder.nameText = (TextView) view.findViewById(R.id.tv_name);
			addOperationToText(holder);
			// 将创建的viewHolder交给view
			view.setTag(holder);
		}

		// 添加逻辑操作
		private static void addOperationToText(ViewHolder holder) {
			holder.callText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 添加具体的逻辑操作
				}
			});

			holder.delText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 添加具体的逻辑操作
				}
			});
		}
	}
}
