package com.wt.springboot.wechat.demo;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 * @date 2020-07-14 下午 2:08
 * introduction
 */
@AllArgsConstructor
@RequestMapping("/wx/jsapi/{appid}")
@Controller
public class WxJsApiDemoController {

    private final WxMpService wxService;

    @GetMapping("/jsPage")
    public String jsPage() {
        return "js_api";
    }

    @GetMapping("/getJsapiTicket")
    @ResponseBody
    public WxJsapiSignature getJsapiTicket(@PathVariable String appid, @RequestParam("url") String url) throws WxErrorException {
        return this.wxService.switchoverTo(appid).createJsapiSignature(url);
    }

}
