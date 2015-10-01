package midascon.example.scanlist;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.BeaconCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

public class MainActivity extends Activity implements BeaconCallback, Runnable {

	private ContextManager contextManager;
	private BeaconListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		contextManager = getMidasApplication().getContextManager();

		adapter = new BeaconListAdapter(getBaseContext());

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);
	}

	@Override
	public void onBeaconCallback(int status, Beacon beacon) {
		switch (status) {
		case STATUS_CODE_ENTER:
		case STATUS_CODE_UPDATE:
			if (adapter != null)
				adapter.addBeacon(beacon);
			break;

		case STATUS_CODE_EXIT:
			if (adapter != null)
				adapter.removeBeacon(beacon);
			break;

		default:
			break;
		}

		runOnUiThread(this);

	}

	@Override
	public void run() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	// AndroidManifest.xml에 설정된 name 클래스 호출
	public MidasApplication getMidasApplication() {
		return (MidasApplication) getApplication();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			// 콜백 등록
			contextManager.setBeaconCallback(this);
			contextManager.startLeScan();
		} else {
			contextManager.stopLeScan();

			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
			startActivity(settingsIntent);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		contextManager.stopLeScan();
	}

}
