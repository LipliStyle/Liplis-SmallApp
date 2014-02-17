//=======================================================================
//  ClassName : LiplisSmallAppConfigurationTopic
//  概要      : 話題設定画面
//
//	extends   : LiplisWidgetConfigurationTopic
//	impliments:
//
//	レイアウトファイル : configuration.xml
//
//  プリファレンスには変換値ではなく、設定値をのものを入力する!
//
//2013/04/29 ver3.3.7 ニュースレンジの追加
//
//  LiplisAndroidシステム
//  Copyright(c) 2011-2013 sachin. All Rights Reserved.
//=======================================================================
package little.cute.Activity;

import little.cute.R;
import little.cute.Common.LiplisDefine;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LiplisSmallAppConfigurationTopic extends LiplisWidgetConfigurationTopic {

    ///====================================================================
    ///
    ///                             初期化処理
    ///
    ///====================================================================

    /// <summary>
    /// onCreate
    /// 作成時処理
    /// </summary>
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//親クラスの
        super.onCreate(savedInstanceState);
    }

    /// <summary>
    /// onCreateOptionsMenu
    /// メニューボタンの初期化
    /// </summary>
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_setting, menu);
      return true;
    }

    /// <summary>
    /// onOptionsItemSelected
    /// オプションアイテムメニューの選択
    /// </summary>
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.menu_setting)
		{
	        setDefault();
		}

		return false;
	}

    ///====================================================================
    ///
    ///                 設定反映のためのブロードキャスト
    ///
    ///====================================================================

    /// <summary>
    /// sendBroadcast
    /// ウィジェットにブロードキャスト送信
    /// </summary>
    @Override
    protected void sendBroadcastSettingEnd()
    {
    	Intent intent = new Intent();
    	intent.setAction(LiplisDefine.LIPLIS_SETTING_FINISH);
    	sendBroadcast(intent);
    }

    /// <summary>
    /// sendBroadcast
    /// ウィジェットにブロードキャスト送信
    /// </summary>
    @Override
    protected void sendBroadcastStart()
    {
    	Intent intent = new Intent();
    	intent.setAction(LiplisDefine.LIPLIS_SETTING_START);
    	sendBroadcast(intent);
    }
}