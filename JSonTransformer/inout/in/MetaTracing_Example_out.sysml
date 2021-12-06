connection rq1ToWing connect eDroneMaxSpeed to eDrone.engine;
metadata m3522: TraceType about rq1ToWing { tracetype = "Untyped";}
connection eng2front connect eDrone.engine to eDrone.frontAxis;
metadata m5377: ConfidenceTracing about eng2front { confidence = 0.55;}
metadata m1664: TraceType about eng2front { tracetype = "typeC";}
connection frAx2frWi connect eDrone.frontAxis to eDrone.frontWing;
metadata m2891: ConfidenceTracing about frAx2frWi { confidence = 0.65;}
metadata m2531: TraceType about frAx2frWi { tracetype = "typeE";}
connection reAx2reWi connect eDrone.rearAxis to eDrone.rearWing;
metadata m6580: TraceType about reAx2reWi { tracetype = "Untyped";}
connection bod2bat connect eDrone.body to eDrone.battery;
metadata m1403: TraceType about bod2bat { tracetype = "Untyped";}
connection bod2eng connect eDrone.body to eDrone.engine;
metadata m7144: TraceType about bod2eng { tracetype = "Untyped";}
connection bod2frAx connect eDrone.body to eDrone.frontAxis;
metadata m9860: TraceType about bod2frAx { tracetype = "Untyped";}
connection bod2reAx connect eDrone.body to eDrone.rearAxis;
metadata m8818: TraceType about bod2reAx { tracetype = "Untyped";}
connection eng2bat_Typed connect eDrone.battery.powerOut to eDrone.engine.powerIn;
metadata m5530: TraceType about eng2bat_Typed { tracetype = "typeE";}metadata m2652: TraceType about eng2bat_Typed { tracetype = "typeC";}
