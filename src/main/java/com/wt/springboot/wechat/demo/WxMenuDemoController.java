package com.wt.springboot.wechat.demo;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Administrator
 * @date 2020-07-13 下午 4:42
 * introduction
 */
@RestController
@RequestMapping("/wx/menu/{appid}")
public class WxMenuDemoController {

    @Autowired
    private WxMpService wxService;

    /**
     * <pre>
     * 自定义菜单创建接口
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013&token=&lang=zh_CN
     * 如果要创建个性化菜单，请设置matchrule属性
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1455782296&token=&lang=zh_CN
     * </pre>
     *
     * @return 如果是个性化菜单，则返回menuid，否则返回null
     */
    @PostMapping("/create")
    public String menuCreate(@PathVariable String appid, @RequestBody WxMenu menu) throws WxErrorException {
        return this.wxService.switchoverTo(appid).getMenuService().menuCreate(menu);
    }

    @GetMapping("/create")
    public String menuCreateSample(@PathVariable String appid) throws WxErrorException, MalformedURLException {
        WxMenu wxMenu = new WxMenu();
        WxMenuButton button1 = new WxMenuButton();
        wxMenu.getButtons().add(button1);
        button1.setType(WxConsts.MenuButtonType.VIEW);
        button1.setName("首页");
        button1.setUrl("http://www.baidu.com");
        WxMenuButton button2 = new WxMenuButton();
        wxMenu.getButtons().add(button2);
        button2.setType(WxConsts.MenuButtonType.VIEW);
        button2.setName("动态");
        WxMenuButton button3 = new WxMenuButton();
        wxMenu.getButtons().add(button3);
        button3.setType(WxConsts.MenuButtonType.VIEW);
        button3.setName("我的");
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            URL requestURL = new URL(request.getRequestURL().toString());
            String url2 = String.format("%s://%s/wx/jsapi/%s/jsPage", requestURL.getProtocol(), requestURL.getHost(), appid);
            button2.setUrl(url2);
            String url3 = this.wxService.switchoverTo(appid).oauth2buildAuthorizationUrl(
                    String.format("%s://%s/wx/redirect/%s/greet", requestURL.getProtocol(), requestURL.getHost(), appid),
                    WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
            button3.setUrl(url3);
        }
        return this.wxService.switchoverTo(appid).getMenuService().menuCreate(wxMenu);
    }
}
