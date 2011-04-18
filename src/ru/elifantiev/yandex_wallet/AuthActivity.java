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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ru.elifantiev.yandex.api.money.YandexMoneyPermissions;
import ru.elifantiev.yandex.oauth.*;
import ru.elifantiev.yandex_wallet.pincode.PinCodeHolder;

public class AuthActivity extends OAuthActivity {

    @Override
    protected PermissionsScope getRequiredPermissions() {
        return new YandexMoneyPermissions(
                YandexMoneyPermissions.PERMISSION_ACCOUNT_INFO |
                        YandexMoneyPermissions.PERMISSION_OPERATION_HISTORY |
                        YandexMoneyPermissions.PERMISSION_OPERATION_DETAILS);
    }

    @Override
    protected void authorize() {
        if(!PinCodeHolder.getInstance().getPin().equals(""))
            super.authorize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getData() == null) { // First run, not a continuation
            showDialog(0);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.pincode);
            dialog.setTitle(getString(R.string.createPinTitle));

            ((TextView) dialog.findViewById(R.id.txtPinFieldTitle)).setText(getString(R.string.enterPinCreate));
            final EditText pin = (EditText) dialog.findViewById(R.id.txtPin);
            Button btnOk = (Button) dialog.findViewById(R.id.btnOk);

            btnOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String pinCode = pin.getText().toString();
                    if (!pinCode.equals("")) {
                        PinCodeHolder.getInstance().setPin(pinCode);
                        dialog.dismiss();
                        authorize();
                    }
                }
            });

            return dialog;
        }
        return null;
    }

    @Override
    protected String getClientId() {
        return "59EA8C77AF05E627FAE23EDD0740AE318A6F758DA4D03E6ED144017BC8C771AF";
    }

    @Override
    protected String getAppId() {
        return getString(R.string.app_id);
    }

    @Override
    protected Uri getServer() {
        return Uri.parse("https://m.sp-money.yandex.ru");
    }

    @Override
    protected AsyncContinuationHandler getContinuationHandler() {
        return new MyContinuationHandler(getClientId(), getDefaultStatusHandler());
    }

    @Override
    protected AccessTokenStorage getTokenStorage() {
        return new EncryptedSharedPreferencesStorage(PinCodeHolder.getInstance().getPin(), this);
    }

    class MyContinuationHandler extends AsyncContinuationHandler {

        private ProgressDialog progressDialog;

        public MyContinuationHandler(String clientId, AuthStatusHandler statusHandler) {
            super(clientId, statusHandler);
        }

        @Override
        protected void onPostExecute(AuthResult result) {
            progressDialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AuthActivity.this, "Authorizing", "Please wait", true, false);
        }
    }


}
