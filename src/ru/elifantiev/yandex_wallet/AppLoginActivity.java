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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ru.elifantiev.yandex.oauth.OAuthActivity;
import ru.elifantiev.yandex_wallet.pincode.PinCodeHolder;

public class AppLoginActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if(intent != null && OAuthActivity.ACTION_AUTH_RESULT.equals(intent.getAction())) {
            if(intent.getIntExtra(OAuthActivity.EXTRA_AUTH_RESULT, -1) != OAuthActivity.AUTH_RESULT_OK) {
                Toast.makeText(
                        AppLoginActivity.this,
                        getIntent().getStringExtra(OAuthActivity.EXTRA_AUTH_RESULT_ERROR),
                        Toast.LENGTH_LONG).show();
            } else {
                getSharedPreferences("appState", 0).edit().putInt("accesgranted", 1).commit();
                goNext();
                return;
            }
        }

        if(getSharedPreferences("appState", 0).getInt("accesgranted", 0) == 0) {
            setContentView(R.layout.entry);
            findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    startActivity(new Intent(AppLoginActivity.this, AuthActivity.class));
                    AppLoginActivity.this.finish();
                }
            });
        } else {
            showDialog(0);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == 0) {
            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.pincode);
            dialog.setTitle(getString(R.string.enterPinTitle));

            final EditText pin = (EditText) dialog.findViewById(R.id.txtPin);
            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            dialog.findViewById(R.id.txtPinFieldTitle).setVisibility(View.GONE);

            btnOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String pinCode =  pin.getText().toString();
                    if(!pinCode.equals("")) {
                        PinCodeHolder.getInstance().setPin(pinCode);
                        goNext();
                    }
                }
            });

            return dialog;
        }
        return null;
    }

    private void goNext() {
        startActivity(
                new Intent(
                        AppLoginActivity.this,
                        MainActivity.class));
        AppLoginActivity.this.finish();
    }
}