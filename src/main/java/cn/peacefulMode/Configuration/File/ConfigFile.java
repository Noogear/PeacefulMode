package cn.peacefulMode.Configuration.File;

import cn.peacefulMode.Configuration.Form.Comments;
import cn.peacefulMode.Configuration.Form.ConfigurationFile;
import cn.peacefulMode.Configuration.Form.ConfigurationPart;

public class ConfigFile extends ConfigurationFile {

    @Comments("版本号, 请勿修改")
    public static int version = 0;

    @Comments("总开关")
    public static boolean enabled = true;

    @Comments("是否默认为和平模式")
    public static boolean defaultPeace = true;

    @Comments({"切换为和平模式后有效时间, 单位为秒", "小于等于0时为无限"})
    public static int effectiveTime = 0;

    @Comments({"保存玩家数据", "服务器重启以后依旧保留玩家状态"})
    public static Persistency persistency = new Persistency();

    public static class Persistency extends ConfigurationPart {

        @Comments("开关")
        public boolean enabled = true;

        @Comments("数据保存间隔, 单位为秒")
        public int fileSaveInterval = 300;

    }

}
