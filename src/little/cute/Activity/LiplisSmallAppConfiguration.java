//=======================================================================
//  ClassName : LiplisSmallAppConfiguration
//  概要      : スモールアプリの設定画面
//
//	extends   : LiplisWidgetConfiguration
//	impliments:
//
//	レイアウトファイル : configuration.xml
//
//  プリファレンスには変換値ではなく、設定値をのものを入力する!
//
//  LiplisAndroidシステム
//  Copyright(c) 2011-2013 sachin. All Rights Reserved.
//=======================================================================
package little.cute.Activity;

import little.cute.Common.LiplisDefine;
import android.content.Intent;
import android.os.Bundle;

public class LiplisSmallAppConfiguration extends LiplisWidgetConfiguration {



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
    	Intent intentSmallAppNormal = new Intent();
    	intentSmallAppNormal.setAction(LiplisDefine.LIPLIS_SETTING_FINISH);
		getBaseContext().sendBroadcast(intentSmallAppNormal);
    }


    /// <summary>
    /// sendBroadcast
    /// ウィジェットにブロードキャスト送信
    /// </summary>
    @Override
    protected void sendBroadcastStart()
    {
    	Intent intentSmallAppNormal = new Intent();
    	intentSmallAppNormal.setAction(LiplisDefine.LIPLIS_SETTING_START);
    	sendBroadcast(intentSmallAppNormal);
    }



}