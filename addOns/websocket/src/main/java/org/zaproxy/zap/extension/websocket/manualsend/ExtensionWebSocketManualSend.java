/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2022 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.websocket.manualsend;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.Extension;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ExtensionLoader;
import org.zaproxy.addon.requester.MessageEditorDialog;
import org.zaproxy.zap.extension.websocket.ExtensionWebSocket;

public class ExtensionWebSocketManualSend extends ExtensionAdaptor {

    private static final List<Class<? extends Extension>> DEPENDENCIES =
            Collections.unmodifiableList(Arrays.asList(ExtensionWebSocket.class));

    private ExtensionWebSocket extensionWebSocket;

    private MessageEditorDialog sendDialog;
    private MessageEditorDialog resendDialog;

    @Override
    public String getUIName() {
        return Constant.messages.getString("websocket.manual.ext.name");
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString("websocket.manual.ext.desc");
    }

    @Override
    public List<Class<? extends Extension>> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public void init() {
        super.init();

        ExtensionLoader extLoader = Control.getSingleton().getExtensionLoader();
        extensionWebSocket = extLoader.getExtension(ExtensionWebSocket.class);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        if (!hasView()) {
            return;
        }

        WebSocketPanelSender sender = new WebSocketPanelSender();
        extensionWebSocket.addAllChannelObserver(sender);

        ManualWebSocketSendEditorPanel editorPanel =
                new ManualWebSocketSendEditorPanel(
                        extensionWebSocket.getWebSocketPanel().getChannelsModel(),
                        sender,
                        true,
                        "websocket.manual_send");
        sendDialog = new SendWebSocketMessageEditorDialog(editorPanel);
        sendDialog.load(extensionHook);

        editorPanel =
                new ManualWebSocketSendEditorPanel(
                        extensionWebSocket.getWebSocketPanel().getChannelsModel(),
                        sender,
                        true,
                        "websocket.manual_resend");
        resendDialog = new ResendWebSocketMessageEditorDialog(editorPanel);
        resendDialog.load(extensionHook);
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    @Override
    public void unload() {
        if (sendDialog != null) {
            sendDialog.unload();
        }

        if (resendDialog != null) {
            resendDialog.unload();
        }
    }
}
