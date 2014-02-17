//=======================================================================
//  ClassName : LiplisSmallAppConfigurationVoice
//  概要      : スモールアプリの音声設定画面
//
//	extends   : LiplisWidgetConfigurationVoice
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

import little.cute.Common.LiplisDefine;
import android.content.Intent;
import android.os.Bundle;

public class LiplisSmallAppConfigurationVoice extends LiplisWidgetConfigurationVoice {

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