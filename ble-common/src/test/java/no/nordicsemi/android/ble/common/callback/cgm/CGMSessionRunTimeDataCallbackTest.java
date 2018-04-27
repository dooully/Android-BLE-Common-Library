package no.nordicsemi.android.ble.common.callback.cgm;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import org.junit.Test;

import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.data.MutableData;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class CGMSessionRunTimeDataCallbackTest {
	private boolean called;

	@Test
	public void onContinuousGlucoseMonitorSessionRunTimeReceived_withCrc() {
		final DataReceivedCallback callback = new CGMSessionRunTimeDataCallback() {
			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceived(@NonNull final BluetoothDevice device, final int sessionRunTime, final boolean secured) {
				called = true;
				assertEquals("Session Run Time", 2, sessionRunTime);
				assertTrue(secured);
			}

			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceivedWithCrcError(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				assertEquals("Correct packet but invalid CRC reported", 1, 2);
			}


			@Override
			public void onInvalidDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				assertEquals("Correct packet but invalid data reported", 1, 2);
			}
		};
		final MutableData data = new MutableData(new byte[4]);
		assertTrue(data.setValue(2, Data.FORMAT_UINT16, 0));
		assertTrue(data.setValue(0xC308, Data.FORMAT_UINT16, 2));
		called = false;
		callback.onDataReceived(null, data);
		assertTrue(called);
	}

	@Test
	public void onContinuousGlucoseMonitorSessionRunTimeReceived_noCrc() {
		final DataReceivedCallback callback = new CGMSessionRunTimeDataCallback() {
			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceived(@NonNull final BluetoothDevice device, final int sessionRunTime, final boolean secured) {
				called = true;
				assertEquals("Session Run Time", 2, sessionRunTime);
				assertFalse(secured);
			}

			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceivedWithCrcError(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				assertEquals("Correct packet but invalid CRC reported", 1, 2);
			}


			@Override
			public void onInvalidDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				assertEquals("Correct packet but invalid data reported", 1, 2);
			}
		};
		final Data data = new Data(new byte[] { 2, 0 });
		called = false;
		callback.onDataReceived(null, data);
		assertTrue(called);
	}

	@Test
	public void onContinuousGlucoseMonitorSessionRunTimeReceivedWithCrcError() {
		final DataReceivedCallback callback = new CGMSessionRunTimeDataCallback() {
			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceived(@NonNull final BluetoothDevice device, final int sessionRunTime, final boolean secured) {
				assertEquals("Invalid CRC but correct packet reported", 1, 2);
			}

			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceivedWithCrcError(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				called = true;
			}


			@Override
			public void onInvalidDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				assertEquals("Correct packet but invalid data reported", 1, 2);
			}
		};
		final MutableData data = new MutableData(new byte[4]);
		assertTrue(data.setValue(2, Data.FORMAT_UINT16, 0));
		assertTrue(data.setValue(0xC309, Data.FORMAT_UINT16, 2));
		called = false;
		callback.onDataReceived(null, data);
		assertTrue(called);
	}

	@Test
	public void onInvalidDataReceived() {
		final DataReceivedCallback callback = new CGMSessionRunTimeDataCallback() {
			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceived(@NonNull final BluetoothDevice device, final int sessionRunTime, final boolean secured) {
				assertEquals("Invalid packet but correct packet reported", 1, 2);
			}

			@Override
			public void onContinuousGlucoseMonitorSessionRunTimeReceivedWithCrcError(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				assertEquals("Invalid packet but invalid CRC reported", 1, 2);
			}

			@Override
			public void onInvalidDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
				called = true;
			}
		};
		final MutableData data = new MutableData(new byte[3]);
		assertTrue(data.setValue(2, Data.FORMAT_UINT16, 0));
		assertTrue(data.setValue(1, Data.FORMAT_UINT8, 2));
		called = false;
		callback.onDataReceived(null, data);
		assertTrue(called);
	}
}