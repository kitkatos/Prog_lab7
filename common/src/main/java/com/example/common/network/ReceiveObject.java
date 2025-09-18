package com.example.common.network;

import java.net.InetSocketAddress;

public record ReceiveObject(NetworkObject object, InetSocketAddress address) {
}
