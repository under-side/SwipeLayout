package org.under_side.swipelayout;

import java.util.ArrayList;

import org.under_side.swipelayout.adapter.ListViewAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.example.swipelayout.R;

public class MainActivity extends Activity {

	public static final String[] NAMES = new String[] { "�ν�", "¬����", "����",
			"����ʤ", "��ʤ", "�ֳ�", "����", "������", "����", "���", "��Ӧ", "����", "³����",
			"����", "��ƽ", "����", "��־", "����", "����", "����", "����", "����", "ʷ��", "�º�",
			"�׺�", "�", "��С��", "�ź�", "��С��", " ��˳", "��С��", "����", "ʯ��", "����",
			" �ⱦ", "����", "����", "����", "����", "����", "��˼��", "����", "��^", "��͢��",
			"κ����", "����", "����", "ŷ��", "�˷�", " ��˳", "����", "����", "����", "����",
			"�� ʢ", "����ȫ", "�ʸ���", "��Ӣ", "������", "����", "����", "����", "����", "���",
			"����", "����", "����", "ͯ��", "ͯ��", "�Ͽ�", "�", "�´�", "�", "֣����",
			"������", "����", "�ֺ�", "����", "������", "�´�", "����", "����", "��Ǩ", "Ѧ��", "ʩ��",
			"��ͨ", "����", "����", "��¡", "��Ԩ", "����", "�츻", "���", "�̸�", "����", "����",
			"����", "��ͦ", "ʯ��", "����", "�˴�ɩ", "����", "�����", " ������", "������", "��ʤ",
			"ʱǨ", "�ξ���" };
	private ListView mLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initListVeiw();
	}

	private void initListVeiw() {
		mLv = (ListView) findViewById(R.id.lv);
		mLv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mLv.setVerticalScrollBarEnabled(false);
		ArrayList<String> nameList=new ArrayList<String>();
		for (String str : NAMES) {
			nameList.add(str);
		}
		ListViewAdapter myAdapter=new ListViewAdapter(MainActivity.this,nameList);
		mLv.setAdapter(myAdapter);
	}
}
