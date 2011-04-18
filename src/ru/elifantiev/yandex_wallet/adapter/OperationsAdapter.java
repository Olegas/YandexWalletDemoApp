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

package ru.elifantiev.yandex_wallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.elifantiev.yandex.api.money.YandexMoneyOperation;
import ru.elifantiev.yandex.api.money.YandexMoneyOperationHistory;
import ru.elifantiev.yandex_wallet.R;

import java.text.SimpleDateFormat;


public class OperationsAdapter extends BaseAdapter {

    private final Context ctx;
    private final YandexMoneyOperationHistory history;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");

    public OperationsAdapter(Context ctx, YandexMoneyOperationHistory history) {
        this.ctx = ctx;
        this.history = history;
    }

    public int getCount() {
        return history.size();
    }

    public YandexMoneyOperation getItem(int i) {
        return history.get(i);
    }

    public long getItemId(int i) {
        return history.get(i).getId();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View item;
        if(view != null)
            item = view;
        else
            item = LayoutInflater.from(ctx).inflate(R.layout.history_item, viewGroup, false);

        YandexMoneyOperation operation = getItem(i);
        ((TextView)item.findViewById(R.id.txtDate)).setText(format.format(operation.getDate()));
        ((TextView)item.findViewById(R.id.txtTitle)).setText(
                ctx.getString(
                        operation.getDirection() == YandexMoneyOperation.DIRECTION_IN ? R.string.oIncoming : R.string.oOutgoing,
                        operation.getTitle()));
        ((TextView)item.findViewById(R.id.txtAmount)).setText(String.format("%.2f", operation.getAmount()));
        return item;
    }
}
