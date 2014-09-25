$(
  function() {
    $("#favourBtn").on("click",function(){
      var data = $("#favourForm").serialize(); 
      $.post("/favour/words",data).success(function(ret){
        if(ret.status === "ok"){
          $("#favourCount").text(" " + ret.favourCount);
        }
      });
    });
  }
);
