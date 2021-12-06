/* Trace links */
connection testLink95 connect end1 to end2;
connection testLink85 connect end1 to end3;
connection testLink75 connect end1 to end4;
connection testLink65 connect end5 to end6;
connection testLink55 connect end6 to end7;
connection testLink45 connect end7 to end8;
connection testLink35 connect end8 to end7;
connection testLink25 connect end8 to end6;

/* Tracing metadata */
metadata m5474: ConfidenceTracing about testLink95 { confidence = 0.95;}
metadata m7195: TraceType about testLink95 { tracetype = "typeA";}
metadata m8927: ConfidenceTracing about testLink85 { confidence = 0.85;}
metadata m132: TraceType about testLink85 { tracetype = "typeA";}metadata m6914: TraceType about testLink85 { tracetype = "typeC";}
metadata m8339: ConfidenceTracing about testLink75 { confidence = 0.75;}
metadata m7261: TraceType about testLink75 { tracetype = "typeA";}
metadata m1158: ConfidenceTracing about testLink65 { confidence = 0.65;}
metadata m7159: ConfidenceTracing about testLink55 { confidence = 0.55;}
metadata m3347: ConfidenceTracing about testLink45 { confidence = 0.45;}
metadata m9405: TraceType about testLink45 { tracetype = "typeE";}
metadata m7614: ConfidenceTracing about testLink35 { confidence = 0.35;}
metadata m4877: ConfidenceTracing about testLink25 { confidence = 0.25;}
metadata m1014: TraceType about testLink25 { tracetype = "typeE";}
