/*
 * Copyright 2011 Oleg Elifantiev
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.elifantiev.yandex_wallet;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import ru.elifantiev.yandex.api.money.YandexMoneyAccountInfo;
import ru.elifantiev.yandex.api.money.YandexMoneyOperationHistory;
import ru.elifantiev.yandex.api.money.YandexMoneyService;
import ru.elifantiev.yandex.oauth.AccessToken;
import ru.elifantiev.yandex.oauth.EncryptedSharedPreferencesStorage;
import ru.elifantiev.yandex.oauth.EncryptedStorageException;
import ru.elifantiev.yandex.oauth.SharedPreferencesStorage;
import ru.elifantiev.yandex_wallet.adapter.OperationsAdapter;
import ru.elifantiev.yandex_wallet.pincode.PinCodeHolder;

public class MainActivity extends ListActivity {

    TextView txtAccount, txtBalance;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtAccount = (TextView) findViewById(R.id.txtAcc);
        txtBalance = (TextView) findViewById(R.id.txtBalance);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clear:
			new SharedPreferencesStorage(MainActivity.this).clearStorage();
            getSharedPreferences("appState", 0).edit().remove("accesgranted").commit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

    @Override
    protected void onResume() {
        AccessToken token = null;
        String pinCode = PinCodeHolder.getInstance().getPin();
        if (pinCode != null && !pinCode.equals("")) {
            try {
                token = new EncryptedSharedPreferencesStorage(pinCode, this).getToken(getString(R.string.app_id));
            } catch (EncryptedStorageException e) {
                // ignore
            }
        }
        if(token == null) {
            Toast.makeText(this, getString(R.string.incorrectPin), Toast.LENGTH_LONG).show();
            finish();
        } else {
            new AsyncGetAccount().execute(token);
            new AsyncGetHistory().execute(token);
        }

        super.onResume();
    }

    class AsyncGetHistory extends AsyncTask<AccessToken, Void, YandexMoneyOperationHistory> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Getting operation history", "Please wait", true, false);
        }

        @Override
        protected void onPostExecute(YandexMoneyOperationHistory result) {
            progressDialog.dismiss();
            MainActivity.this.getListView().setAdapter(new OperationsAdapter(MainActivity.this, result));
        }

        @Override
        protected YandexMoneyOperationHistory doInBackground(AccessToken... token) {
            try {
                return new YandexMoneyService(token[0]).getOperationHistory();
            } catch (Exception e) {
                // ignore
            }
            return null;
        }
    }


    class AsyncGetAccount extends AsyncTask<AccessToken, Void, YandexMoneyAccountInfo> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Getting account info", "Please wait", true, false);
        }

        @Override
        protected void onPostExecute(YandexMoneyAccountInfo result) {
            progressDialog.dismiss();
            txtAccount.setText(result.getAccount());
            txtBalance.setText(String.valueOf(result.getBalance()));
        }

        @Override
        protected YandexMoneyAccountInfo doInBackground(AccessToken... token) {
            try {
                return new YandexMoneyService(token[0]).getAccountInfo();
            } catch (Exception e) {
                // ignore
            }
            return null;
        }
    }
}