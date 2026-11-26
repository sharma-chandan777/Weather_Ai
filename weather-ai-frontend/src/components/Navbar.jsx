import { useState } from "react";



function Navbar({ activePage, setActivePage }) {

  const [isMobileOpen, setIsMobileOpen] = useState(false);



  const navItems = [

    { id: "home", label: "Dashboard", icon: "" },

    { id: "assistant", label: "AI Assistant", icon: "✨ " },

  ];



  const handleNavClick = (id) => {

    setActivePage(id);

    setIsMobileOpen(false); // Mobile menu close close on click

  };



  return (

    <nav className="navbar">

      <div className="nav-inner">

        {/* Logo */}

        <div

          className="logo"

          onClick={() => handleNavClick("home")}

          role="button"

          tabIndex={0}

        >

          <div className="logo-icon">☁</div>

          <span>WeatherSense</span>

        </div>



        {/* Mobile Toggle Button */}

        <button

          className="mobile-toggle"

          onClick={() => setIsMobileOpen(!isMobileOpen)}

          aria-label="Toggle navigation"

        >

          {isMobileOpen ? "✕" : "☰"}

        </button>



        {/* Dynamic Nav Links */}

        <div className={`nav-links ${isMobileOpen ? "open" : ""}`}>

          {navItems.map((item) => {

            const isActive = activePage === item.id;

            return (

              <button

                key={item.id}

                className={isActive ? "active" : ""}

                onClick={() => handleNavClick(item.id)}

                aria-current={isActive ? "page" : undefined}

              >

                {item.icon}

                {item.label}

              </button>

            );

          })}

        </div>

      </div>

    </nav>

  );

}



export default Navbar; 

