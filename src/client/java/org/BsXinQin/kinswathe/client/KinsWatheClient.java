package org.BsXinQin.kinswathe.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.BsXinQin.kinswathe.KinsWatheEntities;
import org.BsXinQin.kinswathe.client.renderer.CaptureDeviceEntityRenderer;

public class KinsWatheClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //客户端初始化
        KinsWatheInitializeClient.init();
        EntityRendererRegistry.register(KinsWatheEntities.CAPTURE_DEVICE, CaptureDeviceEntityRenderer::new);
    }
}