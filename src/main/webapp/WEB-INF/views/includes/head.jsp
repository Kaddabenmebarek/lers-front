<%--
  Created by IntelliJ IDEA.
  User: Kadda
  Date: 14.12.2020
  Time: 22:12
  To change this template use File | Settings | File Templates.
--%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css" rel="stylesheet" crossorigin="anonymous"/>
    <link rel="stylesheet" type="text/css" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <link href="https://www.jqueryscript.net/css/jquerysctipttop.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="https://code.jquery.com/jquery-3.3.1.min.js" crossorigin="anonymous"></script>
	<!-- <script src="https://code.jquery.com/jquery-1.12.4.js"></script> -->
 	<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

	<!-- highlight datatable -->
	<script type="text/javascript" src="https://cdn.jsdelivr.net/gh/julmot/mark.js@8.11.1/dist/jquery.mark.js"></script>
	<script type="text/javascript" src="https://cdn.jsdelivr.net/gh/julmot/datatables.mark.js@2.1.0/dist/datatables.mark.js"></script>
	

	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.11.3/b-2.0.1/b-colvis-2.0.1/b-html5-2.0.1/b-print-2.0.1/cr-1.5.4/fh-3.2.0/datatables.min.css" crossorigin="anonymous"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/pdfmake.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.36/vfs_fonts.js"></script>
	<script type="text/javascript" src="https://cdn.datatables.net/v/dt/jszip-2.5.0/dt-1.11.3/b-2.0.1/b-colvis-2.0.1/b-html5-2.0.1/b-print-2.0.1/cr-1.5.4/fh-3.2.0/datatables.min.js" crossorigin="anonymous"></script>
	

    <script type="text/javascript" src="./resources/js/instrumentlist.js"></script>
    <!-- Date time picker -->
    <%--<script type="text/javascript" src="./resources/js/luxon.js"></script>--%>
    <!--script type="text/javascript" src="./resources/js/jquery.highlight.js"></script-->

    <!-- Remember to include jQuery :)
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.0.0/jquery.min.js"></script>-->

    <%--<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>--%>

    <!-- Fullcalendar scheduler -->
    <link href="./resources/fullcalendar-scheduler/main.css" rel="stylesheet" />
    <script type="text/javascript" src="./resources/fullcalendar-scheduler/main.js"></script>
    <%--<script type="text/javascript" src="./resources/fullcalendar-scheduler/locales-all.min.js"></script>--%>
    <%--<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/fullcalendar@5.8.0/main.min.css" integrity="sha256-u40zn9KeZYpMjgYaxWJccb4HnP0i8XI17xkXrEklevE=" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/fullcalendar@5.8.0/main.min.js" integrity="sha256-AOrsg7pOO9zNtKymdz4LsI+KyLEHhTccJrZVU4UFwIU=" crossorigin="anonymous"></script>--%>
    <script src="https://cdn.jsdelivr.net/npm/fullcalendar@5.8.0/locales-all.min.js" integrity="sha256-6TW9hevn9VV+Dk6OtclSzIjH05B6f2WWhJ/PQgy7m7s=" crossorigin="anonymous"></script>

    <link rel="icon" type="image/icon" href="./resources/images/calendar-icon.svg">

    <link href="https://fonts.googleapis.com/css?family=Lato:400,400i" rel="stylesheet">

    <!-- BOOTSTRAP -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.39.0/css/tempusdominus-bootstrap-4.min.css" crossorigin="anonymous" />


	<script src="./resources/js/multislider.js"></script>

    <link rel="stylesheet" href="./resources/css/filter_multi_select.css" />

    <!-- Loading spinning wheel -->
    <%--<link type="text/css" rel="stylesheet" href="./resources/css/waitMe.min.css">--%>
    <%--<script src="./resources/js/waitMe.min.js"></script>--%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/waitme@1.19.0/waitMe.min.css" integrity="sha256-f4pKuDVe4fH+x/e/ZkA4CgDKOA5SuSlvCnB4BjMb4Ys=" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/waitme@1.19.0/waitMe.min.js" integrity="sha256-oGX4TEGGqGIQgVjZLz74NPm62KtrhR94cxSTRpzcN+o=" crossorigin="anonymous"></script>
    <script language="JavaScript">
        function run_waitMe(){
            $('#calendar').waitMe({

                //none, rotateplane, stretch, orbit, roundBounce, win8,
                //win8_linear, ios, facebook, rotation, timer, pulse,
                //progressBar, bouncePulse or img
                effect: 'roundBounce',

                //place text under the effect (string).
                text: 'Loading...',

                //background for container (string).
                bg: '',

                //color for background animation and text (string).
                color: '',

                //max size
                maxSize: '',

                //wait time im ms to close
                waitTime: -1,

                //url to image
                source: '',

                //or 'horizontal'
                textPos: 'vertical',

                //font size
                fontSize: '',

                // callback
                onClose: function() {}
            });
        }
    </script>

    <meta name=color-scheme content="light dark">
    <link rel="stylesheet" type="text/css" href="./resources/css/styles.css?1.0.2">
    <link rel="stylesheet" type="text/css" href="./resources/css/styles-dark.css?1.0.0" media="screen and (prefers-color-scheme: dark)">

    <!-- date picker -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.29.1/moment.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.39.0/js/tempusdominus-bootstrap-4.min.js" crossorigin="anonymous"></script>

	<!-- multi select -->

    <script src="./resources/js/calendar-util.js"></script>

    <title>DD Planner</title>
</head>
