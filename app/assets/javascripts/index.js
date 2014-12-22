/*
 * Index Javascript
 */

$(document).ready(function () {
    function filter($btn, $ctn) {
        $btn.click(function (e) {
            e.preventDefault();
            var arr = $ctn.data("url").split('?');
            var url = arr[0] + $ctn.val() + "?" + arr[1];
            window.location = url;
        });
    }

    filter($("#hashtag-btn"), $("#hashtag-content"));
    filter($("#location-btn"), $("#location-content"));
    filter($("#username-btn"), $("#username-content"));
});
