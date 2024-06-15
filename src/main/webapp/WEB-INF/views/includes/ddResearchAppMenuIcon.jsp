<%--
  Created by IntelliJ IDEA.
  User: leonada1
  Date: 04.02.2022
  Time: 10:10
  To change this template use File | Settings | File Templates.
--%>

<link rel="stylesheet" type="text/css" href="resources/css/dd.research-app.css" />
<script>
    const fadeTimeout = 200;
    const $DARK_WIDGET_PARAMS = "&dark=true&bg_color=323232&navlink_color=2a2a2a&border_color=2a2a2a";
    let $lastAT = null;
    let $d = false;

    function toggleApplicationsMenu(event){
        updateIframeSrc();
        $('#dd-research-app-menu-widget').fadeToggle(fadeTimeout);
    }

    function updateIframeSrc(){
        let frame = $("#dd-research-app-menu-widget iframe");
        let src = getWidgetUrl();

        let isDark = enabled;
        let originalSrc = frame.attr("src");
        let srcIsDark = originalSrc != undefined && originalSrc.includes("dark");
        let isDarkChanged = (isDark && !srcIsDark) || (!isDark && srcIsDark);
        if(isDarkChanged) {
            src += (isDark ? $DARK_WIDGET_PARAMS : "");
            updateFrameSrc(frame, src);
        }

        $.get({
            url: "sat",
            success: function (at) {
                if($lastAT != at && at.startsWith("AccessTokenO")) {
                    $lastAT = at;
                    let tmpSrc = src + "&" + at;
                    updateFrameSrc(frame, tmpSrc);
                    setTimeout(function (){
                        updateFrameSrc(frame, src);
                    }, fadeTimeout);
                }
            }
        });
    }

    function updateFrameSrc(frame, src){
        //console.log("update frame src by: " + src);
        frame.attr("src", src);
    }

    function getWidgetUrl(){
        let url = location.origin.includes("ares") || location.origin.includes("apollo") ? location.origin : "https://apollo";
        if(location.hostname == ("localhost") && $d){
            url = "http://localhost:8088";
        }
        return url + "/widget/appmenu?fif=true";
    }

    $(document).mouseup(function(e) {
        let containers = $(".hide-on-click-outside");

        containers.each(function () {
            let container = $(this);
            let link = $(e.target).closest("a");
            let isElementCaller = link.length && link.next().closest(container).length;
            // if the target of the click isn't the container nor a descendant of the container
            if (container.is(':visible') && !$(e.target).closest(container).length && !isElementCaller) {
                container.fadeOut(fadeTimeout);
            }
        });
    });

    $(window).ready(function () {
        $('#research-apps-btn').click(toggleApplicationsMenu);
        updateIframeSrc();
    });
</script>
<a class="research-apps-btn nav-link btn-icon" title="DD Research Applications" aria-label="DD Research Applications" aria-expanded="false" role="button" tabindex="0" onclick="toggleApplicationsMenu()">
    <svg class="research-apps-menu-icon" viewBox="0 0 24 24">
        <path d="M6,8c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM12,20c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM6,20c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM6,14c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM12,14c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM16,6c0,1.1 0.9,2 2,2s2,-0.9 2,-2 -0.9,-2 -2,-2 -2,0.9 -2,2zM12,8c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM18,14c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2zM18,20c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2 0.9,2 2,2z"></path>
    </svg>
</a>

<div id="dd-research-app-menu-widget" class="dd-research-app-menu-widget hide-on-click-outside">
    <%--<iframe src="https://apollo/widget/appmenu" title="DD Research Applications"></iframe>--%>
    <iframe title="DD Research Applications"></iframe>
</div>