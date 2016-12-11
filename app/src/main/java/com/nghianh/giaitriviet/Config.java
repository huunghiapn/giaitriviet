package com.nghianh.giaitriviet;

import com.nghianh.giaitriviet.drawer.NavItem;
import com.nghianh.giaitriviet.fav.ui.FavFragment;
import com.nghianh.giaitriviet.providers.facebook.FacebookFragment;
import com.nghianh.giaitriviet.providers.radio.ui.MediaFragment;
import com.nghianh.giaitriviet.providers.rss.ui.RssFragment;
import com.nghianh.giaitriviet.providers.tv.TV_Link;
import com.nghianh.giaitriviet.providers.tv.TvFragment;
import com.nghianh.giaitriviet.providers.yt.ui.VideosFragment;

import java.util.ArrayList;
import java.util.List;

public class Config {

    //To open links in the WebView or outside the WebView.
    public static final boolean OPEN_EXPLICIT_EXTERNAL = true;
    public static final boolean OPEN_INLINE_EXTERNAL = false;

    //To open videos in our Local player or outside the local player
    public static final boolean PLAY_EXTERNAL = false;

    //To use a modern drawer (overlaying toolbar) with header image
    public static final boolean USE_NEW_DRAWER = true;

    //Wordpress perma-friendly API requests (JSON API)
    public static final boolean USE_WP_FRIENDLY = true;

    //If ads are enabled, also show them on the youtube layout
    public static final boolean ADMOB_YOUTUBE = true;


    public static List<NavItem> configuration() {

        List<NavItem> i = new ArrayList<>();

        //DONT MODIFY ABOVE THIS LINE

        //Some sample content is added below, please refer to your documentation for more information about configuring this file properly
        i.add(new NavItem("Today hot news", NavItem.SECTION));

        i.add(new NavItem("VNExpress", R.drawable.vn_expess_icon32x32, NavItem.ITEM, RssFragment.class,
                new String[]{"http://vnexpress.net/rss/tin-moi-nhat.rss"}));
        i.add(new NavItem("Tuoi Tre", R.drawable.tuoitre_icon, NavItem.ITEM, RssFragment.class,
                new String[]{"http://tuoitre.vn/rss/tt-tin-moi-nhat.rss"}));
        i.add(new NavItem("Tin tuc 24h", R.drawable._24h_icon, NavItem.ITEM, RssFragment.class,
                new String[]{"http://www.24h.com.vn/upload/rss/tintuctrongngay.rss"}));
        i.add(new NavItem("", R.drawable.ggnews, NavItem.ITEM, RssFragment.class,
                new String[]{"https://news.google.com/news?cf=all&hl=vi&pz=1&ned=vi_vn&output=rss"}));

        i.add(new NavItem("TV Online", NavItem.SECTION));
        String TV_GROUP[] = {"VN VTV", "VN HTV", "VN VTC", "VN MobiTV", "VN DIA PHUONG", "HAI NGOAI", "QUOC TE TH", "CHINA", "PHIM TH", "SPORTS"};//, "18+"};

        for (int j = 0; j < TV_GROUP.length; j++) {
            i.add(new NavItem(TV_GROUP[j], R.drawable.play_icon, NavItem.ITEM, TvFragment.class,
                    new String[]{TV_Link.LINK01, String.valueOf(j)}));
        }

        i.add(new NavItem("Radio Shoutcast", NavItem.SECTION));
        i.add(new NavItem("VN-VOV", R.drawable.vov_channel_icon, NavItem.ITEM, MediaFragment.class,
                new String[]{"http://stream.mobiradio.vn/vov1", "visualizer"}));

        i.add(new NavItem("Youtube Channel", NavItem.SECTION));

        //i.add(new NavItem("Bong Da", R.drawable.youtube_icon, NavItem.ITEM, VideosFragment.class,
        //new String[]{"PLPYoHdBDRq5VKQReLPCHTsnDlkTgscjox", "UCkCu0hmls6AE72rZxsht3lQ", "0"}));
        i.add(new NavItem("LARVA FUNNY", R.drawable.youtube_icon, NavItem.ITEM, VideosFragment.class,
                new String[]{"PLbxRosGT0CCZOWB7FN1b709EAdFK4NNcW", "UCkCu0hmls6AE72rZxsht3lQ", "1"}));
        //i.add(new NavItem("Trang News", R.drawable.youtube_icon, NavItem.ITEM, VideosFragment.class,
        // new String[]{"PLPYoHdBDRq5VKQReLPCHTsnDlkTgscjox", "UCkCu0hmls6AE72rZxsht3lQ", "1"}));

        /*i.add(new NavItem("Phone billing caculator", NavItem.SECTION));
        i.add(new NavItem("Billing (comming soon)", R.drawable.ic_details, NavItem.ITEM, WebviewFragment.class,
                new String[]{"http://www.nghianguyenit.net/billing/"}));
        i.add(new NavItem("Promotion (comming soon)", R.drawable.ic_details, NavItem.ITEM, WebviewFragment.class,
                new String[]{"http://www.nghianguyenit.net/billing/"}));
        i.add(new NavItem("Recharge (comming soon)", R.drawable.ic_details, NavItem.ITEM, WebviewFragment.class,
                new String[]{"http://www.nghianguyenit.net/billing/"}));*/

        //i.add(new NavItem("Mails", NavItem.SECTION));
        //i.add(new NavItem("Send Mail", R.drawable.mail_icon, NavItem.ITEM, CustomIntent.class, new String[]{"mailto:example@example.com", CustomIntent.OPEN_URL}));

        //i.add(new NavItem("Maps", NavItem.SECTION));
        //i.add(new NavItem("Bus Maps", R.drawable.map_icon, NavItem.ITEM, MapsFragment.class,
        //new String[]{"ho chi minh"}));
        //i.add(new NavItem("Bus Maps", R.drawable.wp_32x32, NavItem.ITEM, WebviewFragment.class,
        //new String[]{"http://www.busmap.vn"}));
        /*i.add(new NavItem("Direction", R.drawable.map_icon, NavItem.ITEM, MapsFragment.class,
                new String[]{"<b>Adress:</b><br>6th Floor, Tower A, WASECO Plaza, 10th Str., Ward, Phổ Quang, Phường 2, Tân Bình<br>Ho Chi Minh, Việt Nam<br><br><i>Email: nghianh@splus-software.com.vn</i>",
                        "Company",
                        "Splus-software JSC",
                        "10.805063",
                        "106.66647",
                        "13"}));*/

        i.add(new NavItem("Social Media", NavItem.SECTION));
        //i.add(new NavItem("Tumblr", R.drawable.ic_details, NavItem.ITEM, TumblrFragment.class,
        //new String[]{"androidbackgrounds"}, true));
        //i.add(new NavItem("SoundCloud", R.drawable.ic_details, NavItem.ITEM, SoundCloudFragment.class,
        // new String[]{"13568105"}));
        //i.add(new NavItem("Twitter", R.drawable.ic_details, NavItem.ITEM, TweetsFragment.class,
        // new String[]{"Android"}));
        //i.add(new NavItem("Instagram", R.drawable.ic_details, NavItem.ITEM, InstagramFragment.class,
        //new String[]{"347696668"}));
        i.add(new NavItem("Facebook fanpage", R.drawable.fb_art, NavItem.ITEM, FacebookFragment.class,
                new String[]{"154557131394991"}));
        //i.add(new NavItem("Wordpress", NavItem.SECTION));
        //i.add(new NavItem("NghiaNguyenIT", R.drawable.ic_details, NavItem.ITEM, WordpressFragment.class,
        //new String[]{"nghianguyenit.net", ""}));
        //i.add(new NavItem("NghiaNguyenIT", R.drawable.wp_32x32, NavItem.ITEM, WebviewFragment.class,
        //new String[]{"http://www.nghianguyenit.net"}));
        //It's suggested to not change the content below this line
        //i.add(new NavItem("Invites", R.drawable.mail_icon, NavItem.ITEM, CustomIntent.class, new String[]{CustomIntent.OPEN_REQUEST_INVITE, CustomIntent.OPEN_REQUEST_INVITE}));

        i.add(new NavItem("Device", NavItem.SECTION));
        i.add(new NavItem("Favorites", R.drawable.ic_action_favorite, NavItem.EXTRA, FavFragment.class, null));
        i.add(new NavItem("Settings", R.drawable.ic_action_settings, NavItem.EXTRA, SettingsFragment.class, null));

        //DONT MODIFY BELOW THIS LINE

        return i;

    }
}