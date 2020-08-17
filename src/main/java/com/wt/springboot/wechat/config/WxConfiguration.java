package com.wt.springboot.wechat.config;

import com.binarywang.spring.starter.wxjava.mp.properties.WxMpProperties;
import com.wt.springboot.wechat.message.handler.*;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceHttpClientImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceJoddHttpImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static me.chanjar.weixin.common.api.WxConsts.EventType.SUBSCRIBE;
import static me.chanjar.weixin.common.api.WxConsts.EventType.UNSUBSCRIBE;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;
import static me.chanjar.weixin.mp.constant.WxMpEventConstants.CustomerService.*;
import static me.chanjar.weixin.mp.constant.WxMpEventConstants.POI_CHECK_NOTIFY;

/**
 * @author Administrator
 * @date 2020-07-13 下午 1:33
 * introduction
 */
@Configuration
public class WxConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LogHandler logHandler() {
        return new LogHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public NullHandler nullHandler() {
        return new NullHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public KfSessionHandler kfSessionHandler() {
        return new KfSessionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public StoreCheckNotifyHandler storeCheckNotifyHandler() {
        return new StoreCheckNotifyHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocationHandler locationHandler() {
        return new LocationHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public MenuHandler menuHandler() {
        return new MenuHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public MsgHandler msgHandler() {
        return new MsgHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public UnsubscribeHandler unsubscribeHandler() {
        return new UnsubscribeHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public SubscribeHandler subscribeHandler() {
        return new SubscribeHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScanHandler scanHandler() {
        return new ScanHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(logHandler()).next();

        // 接收客服会话管理事件
        newRouter.rule().async(false).msgType(EVENT).event(KF_CREATE_SESSION)
                .handler(kfSessionHandler()).end();
        newRouter.rule().async(false).msgType(EVENT).event(KF_CLOSE_SESSION)
                .handler(kfSessionHandler()).end();
        newRouter.rule().async(false).msgType(EVENT).event(KF_SWITCH_SESSION)
                .handler(kfSessionHandler()).end();

        // 门店审核事件
        newRouter.rule().async(false).msgType(EVENT).event(POI_CHECK_NOTIFY).handler(storeCheckNotifyHandler()).end();

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(EVENT).event(WxConsts.EventType.CLICK).handler(menuHandler()).end();

        // 点击菜单连接事件
        newRouter.rule().async(false).msgType(EVENT).event(WxConsts.EventType.VIEW).handler(nullHandler()).end();

        // 关注事件
        newRouter.rule().async(false).msgType(EVENT).event(SUBSCRIBE).handler(subscribeHandler()).end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(EVENT).event(UNSUBSCRIBE).handler(unsubscribeHandler()).end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(EVENT).event(WxConsts.EventType.LOCATION).handler(locationHandler()).end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION).handler(locationHandler()).end();

        // 扫码事件
        newRouter.rule().async(false).msgType(EVENT).event(WxConsts.EventType.SCAN).handler(scanHandler()).end();

        // 默认
        newRouter.rule().async(false).handler(msgHandler()).end();

        return newRouter;
    }

    @Configuration
    static class EnableWxConfiguration{

        @Bean
        public WxMpService wxMpService(WxMpConfigStorage configStorage, WxMpProperties wxMpProperties) {
            WxMpProperties.HttpClientType httpClientType = wxMpProperties.getConfigStorage().getHttpClientType();
            WxMpService wxMpService;
            if (httpClientType == WxMpProperties.HttpClientType.okhttp) {
                wxMpService = newWxMpServiceJoddHttpImpl();
            } else if (httpClientType == WxMpProperties.HttpClientType.joddhttp) {
                wxMpService = newWxMpServiceOkHttpImpl();
            } else if (httpClientType == WxMpProperties.HttpClientType.httpclient) {
                wxMpService = newWxMpServiceHttpClientImpl();
            } else {
                wxMpService = newWxMpServiceImpl();
            }
            WxMpConfigStorageHolder.set(configStorage.getAppId());
            wxMpService.setWxMpConfigStorage(configStorage);
            return wxMpService;
        }

        private WxMpService newWxMpServiceImpl() {
            return new WxMpServiceImpl();
        }

        private WxMpService newWxMpServiceHttpClientImpl() {
            return new WxMpServiceHttpClientImpl();
        }

        private WxMpService newWxMpServiceOkHttpImpl() {
            return new WxMpServiceOkHttpImpl();
        }

        private WxMpService newWxMpServiceJoddHttpImpl() {
            return new WxMpServiceJoddHttpImpl();
        }
    }



}
