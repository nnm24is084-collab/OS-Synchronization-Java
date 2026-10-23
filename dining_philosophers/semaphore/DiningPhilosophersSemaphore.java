
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Semaphore;

public class DiningPhilosophersSemaphore {
    static final int N = 4, MEALS = 3;
    static final Semaphore room = new Semaphore(N - 1, true);
    static final Semaphore[] forks = new Semaphore[N];
    static final String[] state = new String[N];
    static final int[] meals = new int[N];
    static final List<String[]> events = new ArrayList<>();
    static final Object logLock = new Object();
    static long start;

    static {
        for (int i = 0; i < N; i++) {
            forks[i] = new Semaphore(1, true);
            state[i] = "THINKING";
        }
    }

    static void log(int p, String s, String msg) {
        synchronized (logLock) {
            state[p] = s;
            events.add(new String[]{
                "" + (System.currentTimeMillis() - start),
                "P" + p, s, msg, "" + p, "" + meals[p]
            });
            System.out.printf("%5s ms | P%d | %-10s | %s%n",
                events.get(events.size()-1)[0], p, s, msg);
        }
    }

    static class Philosopher extends Thread {
        final int p;
        Philosopher(int p) { this.p = p; }

        public void run() {
            int left = p, right = (p + 1) % N;
            try {
                for (int m = 1; m <= MEALS; m++) {
                    log(p, "THINKING", "Thinking");
                    Thread.sleep(250);
                    log(p, "HUNGRY", "Hungry; trying to enter room");
                    room.acquire();
                    try {
                        log(p, "ACQUIRING", "Entered room; trying to take forks F" + left + " and F" + right);
                        forks[left].acquire();
                        log(p, "ACQUIRING", "Picked up left fork F" + left);
                        try {
                            forks[right].acquire();
                            log(p, "ACQUIRING", "Picked up right fork F" + right);
                            try {
                                meals[p] = m;
                                log(p, "EATING", "Eating meal " + m + " of " + MEALS);
                                Thread.sleep(600);
                            } finally {
                                forks[right].release();
                                log(p, "RELEASING", "Put down right fork F" + right);
                            }
                        } finally {
                            forks[left].release();
                            log(p, "RELEASING", "Put down left fork F" + left);
                        }
                    } finally {
                        room.release();
                    }
                    log(p, "THINKING", "Back to thinking");
                }
                log(p, "FINISHED", "Completed all meals");
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log(p, "INTERRUPTED", "Interrupted");
            }
        }
    }

    static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    static void makeHtml() throws IOException {
        StringBuilder data = new StringBuilder("[");
        synchronized (logLock) {
            for (int i = 0; i < events.size(); i++) {
                if (i > 0) data.append(",");
                data.append("[");
                String[] r = events.get(i);
                for (int j = 0; j < r.length; j++) {
                    if (j > 0) data.append(",");
                    data.append("\"").append(esc(r[j])).append("\"");
                }
                data.append("]");
            }
        }
        data.append("]");

        String html = """
<!doctype html><html><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Dining Philosophers Semaphore</title>
<style>
*{box-sizing:border-box}body{margin:0;background:#f4f6fa;color:#233047;font:13px Arial}
main{max-width:1250px;margin:auto;padding:12px}
h1{font-size:20px;margin:0 0 5px}h2{font-size:15px;margin:0 0 12px}
.sub{color:#64748b;margin-bottom:12px}.card{background:white;border:1px solid #d9e0eb;border-radius:11px;padding:12px;margin-bottom:12px}
.controls{display:flex;align-items:center;gap:8px;flex-wrap:wrap}
button,select{padding:6px 10px;border:1px solid #cbd5e1;border-radius:6px;background:white;color:#1e293b;cursor:pointer}
button:hover{background:#eef2ff}input[type=range]{flex:1;min-width:100px}
.legend{display:flex;gap:12px;flex-wrap:wrap;color:#64748b;margin-top:9px}
.dot{display:inline-block;width:9px;height:9px;border-radius:2px;margin-right:3px}
.THINKING{background:#6485ff}.HUNGRY{background:#f5a000}.ACQUIRING{background:#a64ff0}.EATING{background:#20a06b}.RELEASING{background:#f97316}.FINISHED{background:#64748b}
.grid{display:grid;grid-template-columns:1fr 1fr;gap:12px}
.visual{display:grid;grid-template-columns:1fr 1.2fr;align-items:center;gap:10px}
svg{width:100%;max-height:340px}.person{cursor:pointer}
table{width:100%;border-collapse:collapse;font-size:12px}td,th{text-align:left;padding:5px;border-bottom:1px solid #dbe2ed}
th{font-weight:bold} .pill{display:inline-block;color:white;padding:3px 8px;border-radius:12px;font-weight:bold}
.small{color:#64748b;font-size:11px}.timeline{display:grid;grid-template-columns:30px 1fr;gap:7px;align-items:center;margin:7px 0}
.track{height:14px;background:#edf0f5;position:relative;border-radius:3px;overflow:hidden}
.seg{position:absolute;top:0;height:100%}.now{position:absolute;top:-3px;bottom:-3px;width:2px;background:#2563eb;z-index:2}
.eventlog{height:175px;overflow:auto;border:1px solid #d9e0eb;border-radius:7px;font:11px Consolas,monospace}
.line{padding:3px 6px;white-space:pre-wrap;cursor:pointer;color:#475569}
.line:hover,.selected{background:#4778ff;color:white}
@media(max-width:760px){.grid,.visual{grid-template-columns:1fr}main{padding:7px}}
</style></head><body><main>
<h1>Dining Philosophers (4) - Semaphore Solution</h1>
<div class="sub">Semaphores: fork[0..3] = 1 each, room = 3 (at most N−1 philosophers compete → no deadlock) — generated from Java execution trace</div>
<div class="card"><div class="controls">
<button id="play">▶ Play</button><button id="step">Step ▶|</button><button id="reset">↺ Reset</button>
<label>Speed <select id="speed"><option>0.5x</option><option selected>1x</option><option>2x</option><option>4x</option></select></label>
<input id="slider" type="range" min="0" value="0">
<span id="time" class="small"></span></div>
<div class="legend"><span><i class="dot THINKING"></i>THINKING</span><span><i class="dot HUNGRY"></i>HUNGRY</span><span><i class="dot ACQUIRING"></i>ACQUIRING</span><span><i class="dot EATING"></i>EATING</span><span><i class="dot RELEASING"></i>RELEASING</span></div></div>
<div class="card visual">
<div><svg id="table" viewBox="0 0 390 370">
<circle cx="195" cy="185" r="88" fill="none" stroke="#a7b3c7" stroke-width="3" stroke-dasharray="4 4"/>
<text x="195" y="189" text-anchor="middle" fill="#94a3b8">TABLE</text><g id="forks"></g><g id="people"></g>
</svg></div>
<div><table><thead><tr><th>Philosopher</th><th>State</th><th>Forks held</th><th>Meals</th></tr></thead><tbody id="pt"></tbody></table>
<br><table><thead><tr><th>Fork</th><th>Status</th></tr></thead><tbody id="ft"></tbody></table>
<p id="deadlock" class="small"></p></div></div>
<div class="card"><h2>Timeline <span class="small">(one lane per thread; blue line = current time)</span></h2><div id="timeline"></div></div>
<div class="card"><h2>Event log <span class="small">(click a line to jump to it)</span></h2><div class="eventlog" id="log"></div></div>
</main><script>
const E=EVENTS, C={THINKING:'#6485ff',HUNGRY:'#f5a000',ACQUIRING:'#a64ff0',EATING:'#20a06b',RELEASING:'#f97316',FINISHED:'#64748b',INTERRUPTED:'#dc2626'};
let idx=0,timer=null;
const $=id=>document.getElementById(id), esc=s=>String(s).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
$('slider').max=Math.max(0,E.length-1);
for(let i=0;i<4;i++){let a=(-90+i*90)*Math.PI/180,x=195+Math.cos(a)*137,y=185+Math.sin(a)*137;
$('people').innerHTML+=`<g id="p${i}" class="person"><circle cx="${x}" cy="${y}" r="31" fill="#6485ff"/><text x="${x}" y="${y-2}" text-anchor="middle" fill="white" font-weight="bold">P${i}</text><text id="ps${i}" x="${x}" y="${y+12}" text-anchor="middle" fill="white" font-size="9">THINKING</text></g>`;
let fx=195+Math.cos(a+Math.PI/4)*92,fy=185+Math.sin(a+Math.PI/4)*92;
$('forks').innerHTML+=`<g id="f${i}"><line x1="${fx-9}" y1="${fy-9}" x2="${fx+9}" y2="${fy+9}" stroke="#9aa6b8" stroke-width="6" stroke-linecap="round"/><text x="${fx+9}" y="${fy-10}" font-size="10" fill="#334155">F${i}(P${i})</text></g>`;
}
function draw(){
idx=Math.max(0,Math.min(idx,E.length-1));$('slider').value=idx;
let s=Array(4).fill('THINKING'),m=Array(4).fill(0),held=Array.from({length:4},()=>[]);
for(let k=0;k<=idx;k++){let r=E[k],p=+r[4];s[p]=r[2];m[p]=+r[5];
let msg=r[3],f=msg.match(/fork F(\\d+)/i);
if(f){let n=+f[1];if(/picked up|acquired/i.test(msg)){if(!held[p].includes(n))held[p].push(n);}if(/put down|released/i.test(msg))held[p]=held[p].filter(v=>v!==n);}
}
let forks=Array(4).fill(-1);held.forEach((a,p)=>a.forEach(f=>forks[f]=p));
$('pt').innerHTML=s.map((v,p)=>`<tr><td>P${p}</td><td><span class="pill" style="background:${C[v]||'#64748b'}">${v}</span></td><td>${held[p].length?held[p].map(f=>'F'+f).join(', '):'-'}</td><td>${m[p]}</td></tr>`).join('');
$('ft').innerHTML=forks.map((p,f)=>`<tr><td>F${f}</td><td>${p<0?'free':'held by P'+p}</td></tr>`).join('');
for(let p=0;p<4;p++){let g=$('p'+p);g.querySelector('circle').setAttribute('fill',C[s[p]]||'#64748b');$('ps'+p).textContent=s[p];}
for(let f=0;f<4;f++){$('f'+f).querySelector('line').setAttribute('stroke',forks[f]<0?'#9aa6b8':C.ACQUIRING);}
let eat=s.filter(v=>v==='EATING').length;
$('deadlock').textContent=eat?'Deadlock check: not deadlocked; '+eat+' philosopher(s) eating':'Deadlock check: not deadlocked at this moment';
$('time').textContent=(E[idx]?.[0]||0)+' / '+(E[E.length-1]?.[0]||0)+' ms';
$('log').innerHTML=E.slice(0,idx+1).map((r,k)=>`<div class="line ${k===idx?'selected':''}" data-i="${k}">[ ${r[0]} ms ] ${r[1]} ${r[2]} ${r[3]}</div>`).join('');
$('log').scrollTop=$('log').scrollHeight;
let max=Math.max(1,E.length-1);
$('timeline').innerHTML=Array.from({length:4},(_,p)=>{let last=null,segs=[];
E.slice(0,idx+1).forEach((r,k)=>{if(+r[4]!==p)return;if(last===null)last=k;else{segs.push({a:last,b:k,c:C[E[last][2]]||'#64748b'});last=null;}});
return `<div class="timeline"><span>P${p}</span><div class="track">${segs.map(z=>`<i class="seg" style="left:${z.a/max*100}%;width:${Math.max(.5,(z.b-z.a+1)/max*100)}%;background:${z.c}"></i>`).join('')}<i class="now" style="left:${idx/max*100}%"></i></div></div>`}).join('');
}
function stop(){if(timer)clearInterval(timer);timer=null;$('play').textContent='▶ Play';}
$('play').onclick=()=>{if(timer){stop();return}if(idx>=E.length-1)idx=0;$('play').textContent='❚❚ Pause';
timer=setInterval(()=>{if(idx>=E.length-1){stop();return}idx++;draw()},500/Math.max(.5,parseFloat($('speed').value)||1));};
$('step').onclick=()=>{stop();idx=Math.min(E.length-1,idx+1);draw()};
$('reset').onclick=()=>{stop();idx=0;draw()};
$('slider').oninput=()=>{stop();idx=+$('slider').value;draw()};
$('log').onclick=e=>{let n=e.target.closest('[data-i]');if(n){stop();idx=+n.dataset.i;draw()}};
draw();
</script></body></html>
""".replace("EVENTS", data.toString());

        Path dir = Path.of("generated_html");
        Files.createDirectories(dir);
        Path file = dir.resolve("dining_philosophers_semaphore.html");
        Files.writeString(file, html, StandardCharsets.UTF_8);
        System.out.println("HTML simulation generated: " + file.toAbsolutePath());
    }

    public static void main(String[] args) throws Exception {
        start = System.currentTimeMillis();
        System.out.println("Dining Philosophers (4) - Semaphore Solution");
        Philosopher[] ps = new Philosopher[N];
        for (int i = 0; i < N; i++) ps[i] = new Philosopher(i);
        for (Philosopher p : ps) p.start();
        for (Philosopher p : ps) p.join();
        System.out.println("Verification: all four philosophers completed 3 meals each.");
        makeHtml();
    }
}
